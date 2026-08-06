package com.yehorsk.medicalplatformbackend.doctor.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorScheduleId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalTime

@Entity
@Table(
    name = "doctor_schedules",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["doctors_id", "week_day"])
    ]
)
class DoctorScheduleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: DoctorScheduleId? = null,

    @Enumerated(EnumType.STRING)
    var weekDay: WeekDay? = null,

    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime? = null,

    @Column(name = "end_time", nullable = false)
    var endTime: LocalTime? = null,

    @Column(name = "slot_duration_minutes", nullable = false)
    var slotDurationMinutes: Int = 30,

    @Column(name = "break_between_minutes", nullable = false)
    var breakBetweenMinutes: Int = 0,

    @Column(name = "lunch_start")
    var lunchStart: LocalTime? = null,

    @Column(name = "is_working_day")
    var isWorkingDay: Boolean = false,

    @Column(name = "lunch_end")
    var lunchEnd: LocalTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctors_id", nullable = false)
    var doctor: DoctorEntity? = null
)

enum class WeekDay {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
}