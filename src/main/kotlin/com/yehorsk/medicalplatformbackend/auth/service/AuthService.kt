package com.yehorsk.medicalplatformbackend.auth.service

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.common.service.MailService
import com.yehorsk.medicalplatformbackend.common.util.JwtService
import com.yehorsk.medicalplatformbackend.common.util.PasswordEncoder
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorAlreadyExistException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorNotApprovedException
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.MedicalCardEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.RefreshTokenEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.auth.database.repository.RefreshTokenRepository
import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidCredentialsException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidTokenException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.UserAlreadyExistException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.UserDoesNotExistException
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.LoginRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.RegisterRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.AuthenticatedUserResponseDto
import com.yehorsk.medicalplatformbackend.auth.service.mappers.toUserResponseDto
import com.yehorsk.medicalplatformbackend.auth.service.mappers.toUserRole
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.MedicalCardRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val mailService: MailService,
    private val userRepository: UserRepository,
    private val doctorRepository: DoctorRepository,
    private val medicalCardRepository: MedicalCardRepository,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userProvider: CurrentUserProvider,
    private val emailVerificationService: EmailVerificationService
) {

    @Transactional
    fun register(request: RegisterRequestDto): ApiResponse {

        if (userRepository.findByEmail(request.email) != null) {
            throw UserAlreadyExistException()
        }

        val user = UserEntity(
            email = request.email,
            hashedPassword = passwordEncoder.encode(request.password)!!,
            firstName = request.firstName,
            lastName = request.lastName,
            role = request.role.toUserRole()
        )

        if (request.role.toUserRole() == UserRole.DOCTOR) {

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
        userRepository.save(user)
        medicalCard.user = user
        medicalCardRepository.save(medicalCard)

        emailVerificationService.sendEmailVerification(user.email)

        return ApiResponse(
            message = "Registration successful. Please verify your email."
        )
    }

    fun login(request: LoginRequestDto): ApiResponseWithData<AuthenticatedUserResponseDto>{
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

            user.id?.let {
                storeRefreshToken(it, refreshToken)
            }

            ApiResponseWithData(
                data = AuthenticatedUserResponseDto(
                    user = user.toUserResponseDto(),
                    accessToken = accessToken,
                    refreshToken = refreshToken
                ),
                message = "Login successful"
            )
        } ?: throw UserDoesNotExistException()
    }

    @Transactional
    fun refresh(token: String): ApiResponseWithData<AuthenticatedUserResponseDto>{
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

        user.id?.let {
            storeRefreshToken(it, newRefreshToken)
        }

        return ApiResponseWithData(
            data = AuthenticatedUserResponseDto(
                user = user.toUserResponseDto(),
                accessToken = newAccessToken,
                refreshToken = newRefreshToken
            )
        )
    }

    @Transactional
    fun logout(): ApiResponse {
        val userId = userProvider.getCurrentUserId()
        refreshTokenRepository.deleteByUserId(userId)
        return ApiResponse(message = "Logged out successfully")
    }

    private fun storeRefreshToken(userId: UserId, token: String){
        val hashed = hashToken(token)
        val expiresAt = Instant.now().plusMillis(jwtService.refreshTokenValidityMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
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