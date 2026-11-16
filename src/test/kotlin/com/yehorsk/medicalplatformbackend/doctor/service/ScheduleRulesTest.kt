package com.yehorsk.medicalplatformbackend.doctor.service

import com.yehorsk.medicalplatformbackend.doctor.database.entity.WeekDay
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.InvalidScheduleException
import com.yehorsk.medicalplatformbackend.doctor.service.dto.DayScheduleDto
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ScheduleRulesTest {

    private fun day(
        weekday: WeekDay = WeekDay.MONDAY,
        start: String? = "09:00",
        end: String? = "12:00",
        lunchStart: String? = null,
        lunchEnd: String? = null,
        slot: Int? = 30,
        breakBetween: Int? = 0,
        working: Boolean = true
    ) = DayScheduleDto(
        weekday = weekday,
        isWorkingDay = working,
        startTime = start?.let(LocalTime::parse),
        endTime = end?.let(LocalTime::parse),
        lunchStart = lunchStart?.let(LocalTime::parse),
        lunchEnd = lunchEnd?.let(LocalTime::parse),
        slotDurationMinutes = slot,
        breakBetweenMinutes = breakBetween
    )

    @Test
    fun `valid schedule passes`() {
        ScheduleRules.validate(listOf(day(), day(WeekDay.SUNDAY, working = false, start = null, end = null)))
    }

    @Test
    fun `error message names the problem`() {
        val e = assertFailsWith<InvalidScheduleException> {
            ScheduleRules.validate(listOf(day(start = "12:00", end = "09:00")))
        }
        assertEquals("Start time must be before end time for MONDAY", e.message)
    }

    @Test
    fun `lunch needs both ends`() {
        assertFailsWith<InvalidScheduleException> {
            ScheduleRules.validate(listOf(day(lunchStart = "10:00")))
        }
    }

    @Test
    fun `hours shorter than one slot are rejected`() {
        assertFailsWith<InvalidScheduleException> {
            ScheduleRules.validate(listOf(day(start = "09:00", end = "09:20")))
        }
    }

    @Test
    fun `duplicate weekdays are rejected`() {
        assertFailsWith<InvalidScheduleException> {
            ScheduleRules.validate(listOf(day(), day(working = false)))
        }
    }

    @Test
    fun `slots skip lunch and respect breaks`() {
        val slots = ScheduleRules.generateSlots(
            LocalTime.of(9, 0), LocalTime.of(12, 0),
            LocalTime.of(10, 0), LocalTime.of(10, 30),
            slotDurationMinutes = 30, breakBetweenMinutes = 0
        )
        assertEquals(listOf("09:00", "09:30", "10:30", "11:00", "11:30"), slots.map { it.toString() })

        val withBreaks = ScheduleRules.generateSlots(
            LocalTime.of(9, 0), LocalTime.of(10, 30), null, null,
            slotDurationMinutes = 30, breakBetweenMinutes = 15
        )
        assertEquals(listOf("09:00", "09:45"), withBreaks.map { it.toString() })
    }

    @Test
    fun `slots stop at midnight instead of looping`() {
        val slots = ScheduleRules.generateSlots(
            LocalTime.of(22, 0), LocalTime.of(23, 59), null, null,
            slotDurationMinutes = 60, breakBetweenMinutes = 0
        )
        assertEquals(listOf(LocalTime.of(22, 0)), slots)
    }

    @Test
    fun `appointments outside the new schedule are conflicts`() {
        // 2026-11-02 is a Monday, 2026-11-03 a Tuesday
        val onSlot = LocalDateTime.of(2026, 11, 2, 9, 30)
        val removedHour = LocalDateTime.of(2026, 11, 2, 11, 30)
        val removedDay = LocalDateTime.of(2026, 11, 3, 9, 0)

        val conflicts = ScheduleRules.findConflicts(
            listOf(day(end = "11:00")),
            listOf(onSlot, removedHour, removedDay)
        )

        assertEquals(listOf(removedHour, removedDay), conflicts)
    }

    @Test
    fun `unchanged schedule has no conflicts`() {
        val conflicts = ScheduleRules.findConflicts(
            listOf(day()),
            listOf(LocalDateTime.of(2026, 11, 2, 11, 30))
        )
        assertTrue(conflicts.isEmpty())
    }
}
