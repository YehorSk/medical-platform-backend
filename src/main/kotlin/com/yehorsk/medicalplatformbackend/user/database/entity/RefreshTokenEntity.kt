package com.yehorsk.medicalplatformbackend.user.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: UserId,

    @Column(nullable = false)
    var expiresAt: Instant,

    @Column(nullable = false)
    var hashedToken: String,

    @CreationTimestamp
    var createdAt: Instant = Instant.now()

)