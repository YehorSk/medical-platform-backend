package com.yehorsk.medicalplatformbackend.user.service

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.common.util.JwtService
import com.yehorsk.medicalplatformbackend.common.util.PasswordEncoder
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.MedicalCardEntity
import com.yehorsk.medicalplatformbackend.user.database.entity.RefreshTokenEntity
import com.yehorsk.medicalplatformbackend.user.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.user.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.user.database.repository.RefreshTokenRepository
import com.yehorsk.medicalplatformbackend.user.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.user.exceptions.types.DoctorAlreadyExistException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.DoctorNotApprovedException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.InvalidCredentialsException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.InvalidTokenException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.UserAlreadyExistException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.UserDoesNotExistException
import com.yehorsk.medicalplatformbackend.user.service.dto.request.LoginRequestDto
import com.yehorsk.medicalplatformbackend.user.service.dto.request.RegisterRequestDto
import com.yehorsk.medicalplatformbackend.user.service.dto.response.AuthenticatedUserResponseDto
import com.yehorsk.medicalplatformbackend.user.service.dto.response.RegisterResponseDto
import com.yehorsk.medicalplatformbackend.user.service.mappers.toUserResponseDto
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val doctorRepository: DoctorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    @Transactional
    fun register(request: RegisterRequestDto): RegisterResponseDto {

        if (userRepository.findByEmail(request.email) != null) {
            throw UserAlreadyExistException()
        }

        val user = UserEntity(
            email = request.email,
            hashedPassword = passwordEncoder.encode(request.password)!!,
            firstName = request.firstName,
            lastName = request.lastName,
            role = request.role
        )

        if (request.role == UserRole.DOCTOR) {

            val licenseNumber = request.licenseNumber
                ?.takeIf { it.isNotBlank() }
                ?: throw InvalidCredentialsException()

            if (doctorRepository.existsByLicenseNumber(licenseNumber)) {
                throw DoctorAlreadyExistException()
            }

            val doctor = DoctorEntity(
                licenseNumber = licenseNumber
            )

            doctor.user = user
            user.doctor = doctor
        }

        val medicalCard = MedicalCardEntity()
        medicalCard.user = user
        user.medicalCard = medicalCard

        userRepository.save(user)

        return RegisterResponseDto()
    }

    fun login(request: LoginRequestDto): AuthenticatedUserResponseDto{
        val user = userRepository.findByEmail(email = request.email)
            ?: throw InvalidCredentialsException()

        if(!passwordEncoder.matches(request.password, user.hashedPassword)){
            throw InvalidCredentialsException()
        }

        if(user.role == UserRole.DOCTOR && user.doctor?.approved != true){
            throw DoctorNotApprovedException()
        }

        return user.id?.let { id ->
            val accessToken = jwtService.generateAccessToken(id, user.role)
            val refreshToken = jwtService.generateRefreshToken(id, user.role)

            storeRefreshToken(user, refreshToken)

            AuthenticatedUserResponseDto(
                user = user.toUserResponseDto(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserDoesNotExistException()
    }

    @Transactional
    fun refresh(token: String): AuthenticatedUserResponseDto{
        if(!jwtService.validateRefreshToken(token)) throw InvalidTokenException()

        val userId = jwtService.getUserIdFromToken(token)
        val user = userRepository.findById(userId).orElseThrow{
            UserDoesNotExistException()
        }

        val hashed = hashToken(token)
        refreshTokenRepository.findByUserIdAndHashedToken(userId, hashed)
            ?: throw InvalidTokenException()

        refreshTokenRepository.deleteByUserIdAndHashedToken(userId, hashed)

        val newAccessToken = jwtService.generateAccessToken(userId, user.role)
        val newRefreshToken = jwtService.generateRefreshToken(userId, user.role)

        storeRefreshToken(user, newRefreshToken)

        return AuthenticatedUserResponseDto(
            user = user.toUserResponseDto(),
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    fun logout(refreshToken: String) {
        val userId = jwtService.getUserIdFromToken(refreshToken)
        val hashed = hashToken(refreshToken)
        refreshTokenRepository.deleteByUserIdAndHashedToken(userId, hashed)
    }

    private fun storeRefreshToken(user: UserEntity, token: String){
        val hashed = hashToken(token)
        val expiresAt = Instant.now().plusMillis(jwtService.refreshTokenValidityMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                user = user,
                hashedToken = hashed,
                expiresAt = expiresAt
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}