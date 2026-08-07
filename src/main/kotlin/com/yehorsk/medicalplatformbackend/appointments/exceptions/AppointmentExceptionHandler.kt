package com.yehorsk.medicalplatformbackend.appointments.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.toResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AppointmentExceptionHandler {

    @ExceptionHandler(AppointmentNotFoundException::class)
    fun onAppointmentNotFound(e: AppointmentNotFoundException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(AppointmentAlreadyExistsException::class)
    fun onAppointmentAlreadyExists(e: AppointmentAlreadyExistsException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(InvalidAppointmentDateTimeException::class)
    fun onInvalidAppointmentDateTime(e: InvalidAppointmentDateTimeException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(UnauthorizedException::class)
    fun onUnauthorized(e: UnauthorizedException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(InvalidAppointmentStatusTransitionException::class)
    fun onInvalidStatusTransition(e: InvalidAppointmentStatusTransitionException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(SlotAlreadyBookedException::class)
    fun onSlotAlreadyBooked(e: SlotAlreadyBookedException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(SlotNotAvailableException::class)
    fun onSlotNotAvailable(e: SlotNotAvailableException, request: HttpServletRequest) =
        e.toResponse(request)

}

