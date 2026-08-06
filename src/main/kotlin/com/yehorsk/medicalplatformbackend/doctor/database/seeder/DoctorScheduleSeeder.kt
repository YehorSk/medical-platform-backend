package com.yehorsk.medicalplatformbackend.doctor.database.seeder

import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorScheduleEntity
import com.yehorsk.medicalplatformbackend.doctor.database.entity.WeekDay
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorScheduleRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalTime

@Order(8)
@Component
class DoctorScheduleSeeder(
    private val doctorRepository: DoctorRepository,
    private val doctorScheduleRepository: DoctorScheduleRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (doctorScheduleRepository.count() > 0) {
            return
        }

        val doctors = doctorRepository.findAll()

        val workingDays = listOf(
            WeekDay.SUNDAY,
            WeekDay.MONDAY,
            WeekDay.TUESDAY,
            WeekDay.WEDNESDAY,
            WeekDay.THURSDAY,
            WeekDay.FRIDAY,
            WeekDay.SATURDAY
        )

        val schedules = mutableListOf<DoctorScheduleEntity>()

        doctors.forEach { doctor ->
            workingDays.forEach { day ->
                schedules += DoctorScheduleEntity(
                    doctor = doctor,
                    weekDay = day,
                    startTime = LocalTime.of(9, 0),
                    endTime = LocalTime.of(18, 0),
                    slotDurationMinutes = 30,
                    breakBetweenMinutes = 0,
                    isWorkingDay = true,
                    lunchStart = LocalTime.of(13, 0),
                    lunchEnd = LocalTime.of(14, 0)
                )
            }
        }

        doctorScheduleRepository.saveAll(schedules)
    }
}