package com.yehorsk.medicalplatformbackend.doctor.service

import com.yehorsk.medicalplatformbackend.doctor.database.entity.WeekDay
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.InvalidScheduleException
import com.yehorsk.medicalplatformbackend.doctor.service.dto.DayScheduleDto
import java.time.LocalDateTime
import java.time.LocalTime

object ScheduleRules {

    const val DEFAULT_SLOT_DURATION_MINUTES = 30
    const val DEFAULT_BREAK_BETWEEN_MINUTES = 0

    fun validate(schedules: List<DayScheduleDto>) {
        val workingDays = schedules.filter { it.isWorkingDay }

        workingDays.forEach { day ->
            val start = day.startTime
                ?: throw InvalidScheduleException("Start time is required for working day ${day.weekday}")
            val end = day.endTime
                ?: throw InvalidScheduleException("End time is required for working day ${day.weekday}")

            if (!start.isBefore(end)) {
                throw InvalidScheduleException("Start time must be before end time for ${day.weekday}")
            }

            if ((day.lunchStart == null) != (day.lunchEnd == null)) {
                throw InvalidScheduleException("Both lunch start and lunch end must be set for ${day.weekday}")
            }

            if (day.lunchStart != null && day.lunchEnd != null) {
                if (!day.lunchStart.isBefore(day.lunchEnd)) {
                    throw InvalidScheduleException("Lunch start must be before lunch end for ${day.weekday}")
                }
                if (day.lunchStart.isBefore(start) || day.lunchEnd.isAfter(end)) {
                    throw InvalidScheduleException("Lunch break must be within working hours for ${day.weekday}")
                }
            }

            val slotDuration = day.slotDurationMinutes ?: DEFAULT_SLOT_DURATION_MINUTES
            if (slotDuration <= 0) {
                throw InvalidScheduleException("Slot duration must be positive for ${day.weekday}")
            }

            val breakBetween = day.breakBetweenMinutes ?: DEFAULT_BREAK_BETWEEN_MINUTES
            if (breakBetween < 0) {
                throw InvalidScheduleException("Break between slots cannot be negative for ${day.weekday}")
            }

            if (slotsFor(day).isEmpty()) {
                throw InvalidScheduleException("Working hours for ${day.weekday} do not fit a single slot")
            }
        }

        val duplicateWeekdays = schedules
            .groupingBy { it.weekday }
            .eachCount()
            .filter { it.value > 1 }

        if (duplicateWeekdays.isNotEmpty()) {
            throw InvalidScheduleException("Duplicate schedule entries for: ${duplicateWeekdays.keys.joinToString()}")
        }
    }

    fun generateSlots(
        startTime: LocalTime,
        endTime: LocalTime,
        lunchStart: LocalTime?,
        lunchEnd: LocalTime?,
        slotDurationMinutes: Int,
        breakBetweenMinutes: Int
    ): List<LocalTime> {
        // Work in minutes of the day so slots can't wrap around midnight
        fun LocalTime.minutes() = hour * 60 + minute
        val end = endTime.minutes()
        val lunch = if (lunchStart != null && lunchEnd != null) lunchStart.minutes() until lunchEnd.minutes() else null

        val slots = mutableListOf<LocalTime>()
        var time = startTime.minutes()

        while (time + slotDurationMinutes <= end) {
            val slotEnd = time + slotDurationMinutes

            val overlapsLunch = lunch != null && time < lunch.last + 1 && slotEnd > lunch.first

            if (!overlapsLunch) {
                slots.add(LocalTime.of(time / 60, time % 60))
            }

            time = slotEnd + breakBetweenMinutes
        }

        return slots
    }

    /**
     * Returns the appointment times that would no longer fall on a bookable slot
     * if [schedules] became the doctor's weekly schedule.
     */
    fun findConflicts(schedules: List<DayScheduleDto>, appointmentTimes: List<LocalDateTime>): List<LocalDateTime> {
        val slotsByWeekday: Map<WeekDay, Set<LocalTime>> = schedules
            .filter { it.isWorkingDay }
            .associate { it.weekday to slotsFor(it).toSet() }

        return appointmentTimes.filter { dateTime ->
            val weekDay = WeekDay.valueOf(dateTime.dayOfWeek.name)
            val slots = slotsByWeekday[weekDay] ?: return@filter true
            dateTime.toLocalTime().withSecond(0).withNano(0) !in slots
        }
    }

    private fun slotsFor(day: DayScheduleDto): List<LocalTime> = generateSlots(
        startTime = day.startTime!!,
        endTime = day.endTime!!,
        lunchStart = day.lunchStart,
        lunchEnd = day.lunchEnd,
        slotDurationMinutes = day.slotDurationMinutes ?: DEFAULT_SLOT_DURATION_MINUTES,
        breakBetweenMinutes = day.breakBetweenMinutes ?: DEFAULT_BREAK_BETWEEN_MINUTES
    )
}
