package com.yehorsk.medicalplatformbackend.common.service

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration

@Service
class MailService(
    private val mailSender: JavaMailSender,
    @param:Value($$"${spring.mail.from}")
    private val from: String
) {

    fun sendPasswordResetEmail(
        email: String,
        username: String,
        userId: UserId,
        token: String,
        expiresIn: Duration
    ){
        sendPlainText(
            to = email,
            subject = "Reset your password",
            body = "Hello $username this is your token $token it expires in $expiresIn"
        )
    }

    fun sendVerificationEmail(
        email: String,
        username: String,
        userId: UserId,
        token: String
    ){
        val verificationLink = UriComponentsBuilder
            .newInstance()
            .scheme("medicalplatform")
            .host("app")
            .path("/verify-email")
            .queryParam("token", token)
            .build()
            .toUriString()

        sendPlainText(
            to = email,
            subject = "Verify your email",
            body = "Click here: $verificationLink"
        )
    }

    fun sendPlainText(to: String, subject: String, body: String) {
        println("Sending email to $to")
        val message = SimpleMailMessage()
        message.setTo(to)
        message.from = from
        message.subject = subject
        message.text = body
        mailSender.send(message)
    }

    fun sendHtml(to: String, subject: String, body: String) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "UTF-8")
        helper.setFrom(from)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(body, true)
        mailSender.send(message)
    }

}