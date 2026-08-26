package com.yehorsk.medicalplatformbackend.notification.controller

import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.notification.controller.dto.DeviceTokenDto
import com.yehorsk.medicalplatformbackend.notification.controller.dto.RegisterDeviceRequest
import com.yehorsk.medicalplatformbackend.notification.mappers.toDeviceTokenDto
import com.yehorsk.medicalplatformbackend.notification.mappers.toPlatformDto
import com.yehorsk.medicalplatformbackend.notification.service.PushNotificationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notification")
class DeviceTokenController(
    private val pushNotificationService: PushNotificationService,
    private val currentUserProvider: CurrentUserProvider
) {

    @PostMapping("/register")
    fun registerDeviceToken(
        @Valid @RequestBody body: RegisterDeviceRequest
    ): DeviceTokenDto {
        val userId = currentUserProvider.getCurrentUserId()
        return pushNotificationService.registerDevice(
            userId = userId,
            token = body.token,
            platform = body.platform.toPlatformDto()
        ).toDeviceTokenDto()
    }

    @DeleteMapping("/{token}")
    fun unregisterDeviceToken(
        @PathVariable("token") token: String
    ) {
        pushNotificationService.unregisterDevice(token)
    }
}