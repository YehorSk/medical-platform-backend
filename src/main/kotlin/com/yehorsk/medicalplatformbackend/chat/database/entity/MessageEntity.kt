package com.yehorsk.medicalplatformbackend.chat.database.entity

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.domain.type.MessageId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant

@Entity
@Table(
    name = "messages",
    indexes = [
        Index(
            name = "idx_messages_conversation_id_created_at",
            columnList = "conversation_id, created_at"
        )
    ]
)
class MessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: MessageId? = null,
    @Column(
        name = "conversation_id",
        nullable = false,
        updatable = false
    )
    var conversationId: ConversationId,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "conversation_id",
        nullable = false,
        insertable = false,
        updatable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var conversation: ConversationEntity? = null,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "sender_id",
        nullable = false,
        insertable = false,
        updatable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var sender: UserEntity? = null,
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),
    @Column(nullable = false)
    var content: String,
)