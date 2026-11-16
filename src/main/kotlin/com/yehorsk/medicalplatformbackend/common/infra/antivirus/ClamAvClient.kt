package com.yehorsk.medicalplatformbackend.common.infra.antivirus

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket

@ConfigurationProperties("clamav")
data class ClamAvProperties(
    val host: String = "localhost",
    val port: Int = 3310,
    val connectTimeoutMs: Int = 5_000,
    // Large files take a while; clamd answers only after the whole stream is scanned
    val readTimeoutMs: Int = 120_000,
    // Must stay below clamd's StreamMaxLength (25 MB by default)
    val chunkSize: Int = 64 * 1024,
)

sealed interface ScanResult {
    data object Clean : ScanResult
    data class Infected(val signature: String) : ScanResult
}

class ClamAvException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

@Component
class ClamAvClient(
    private val props: ClamAvProperties
) {

    fun scan(input: InputStream): ScanResult {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(props.host, props.port), props.connectTimeoutMs)
                socket.soTimeout = props.readTimeoutMs

                val out = DataOutputStream(socket.getOutputStream().buffered())
                out.write("zINSTREAM\u0000".toByteArray(Charsets.US_ASCII))

                val buffer = ByteArray(props.chunkSize)
                while (true) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    if (read == 0) continue
                    out.writeInt(read)
                    out.write(buffer, 0, read)
                }
                out.writeInt(0)
                out.flush()

                return parseReply(readReply(socket.getInputStream()))
            }
        } catch (e: ClamAvException) {
            throw e
        } catch (e: IOException) {
            throw ClamAvException("ClamAV communication failed: ${e.message}", e)
        }
    }

    fun ping(): Boolean = try {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(props.host, props.port), props.connectTimeoutMs)
            socket.soTimeout = props.connectTimeoutMs
            socket.getOutputStream().apply { write("zPING\u0000".toByteArray(Charsets.US_ASCII)); flush() }
            readReply(socket.getInputStream()) == "PONG"
        }
    } catch (e: IOException) {
        false
    }

    private fun readReply(input: InputStream): String {
        val reply = ByteArrayOutputStream()
        while (true) {
            val b = input.read()
            if (b == -1 || b == 0) break
            reply.write(b)
        }
        return reply.toString(Charsets.US_ASCII).trim()
    }

    private fun parseReply(reply: String): ScanResult = when {
        reply.endsWith("OK") && !reply.endsWith("ERROR") -> ScanResult.Clean
        reply.endsWith("FOUND") -> ScanResult.Infected(
            reply.removePrefix("stream:").removeSuffix("FOUND").trim()
        )
        else -> throw ClamAvException("ClamAV returned an error: $reply")
    }
}
