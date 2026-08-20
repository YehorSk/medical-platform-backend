package com.yehorsk.medicalplatformbackend.chat.database.repository

import com.yehorsk.medicalplatformbackend.chat.database.entity.MessageEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.domain.type.MessageId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.Query

interface MessageRepository : JpaRepository<MessageEntity, MessageId> {

    fun findByConversationIdOrderByCreatedAtDesc(
        conversationId: ConversationId,
        pageable: Pageable
    ): Slice<MessageEntity>

    @Query("""
        SELECT m
        FROM MessageEntity m
        LEFT JOIN FETCH m.sender
        WHERE m.conversationId IN :conversationIds
        AND (m.createdAt, m.id) = (
            SELECT m2.createdAt, m2.id
            FROM MessageEntity m2
            WHERE m2.conversationId = m.conversationId
            ORDER BY m2.createdAt DESC
            LIMIT 1
        )
    """)
    fun findLatestMessagesByConversationIds(
        conversationIds: Set<ConversationId>
    ): List<MessageEntity>

}