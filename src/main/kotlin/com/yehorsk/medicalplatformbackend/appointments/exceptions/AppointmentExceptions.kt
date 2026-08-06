package com.yehorsk.medicalplatformbackend.appointments.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class AppointmentNotFoundException : AppException("APPOINTMENT_NOT_FOUND", HttpStatus.NOT_FOUND, "Appointment not found")

class AppointmentAlreadyExistsException : AppException("APPOINTMENT_ALREADY_EXISTS", HttpStatus.CONFLICT, "Appointment at this time already exists")

class InvalidAppointmentDateTimeException : AppException("INVALID_APPOINTMENT_DATE_TIME", HttpStatus.BAD_REQUEST, "Appointment date and time must be in the future")

class UnauthorizedException : AppException("UNAUTHORIZED", HttpStatus.FORBIDDEN, "You are not authorized to perform this action")

class InvalidAppointmentStatusTransitionException(from: String, to: String) :
    AppException("INVALID_STATUS_TRANSITION", HttpStatus.BAD_REQUEST, "Cannot transition appointment status from $from to $to")

class SlotAlreadyBookedException: AppException("SLOT_ALREADY_BOOKED", HttpStatus.CONFLICT, "This time slot is already booked")
