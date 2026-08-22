package com.yehorsk.medicalplatformbackend.chat.database.repository

import com.yehorsk.medicalplatformbackend.chat.database.entity.ConversationEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ConversationRepository : JpaRepository<ConversationEntity, ConversationId> {

    @Query("""
        SELECT c 
        FROM ConversationEntity c 
        WHERE c.patient.id = :userId OR c.doctor.id = :userId
        ORDER BY c.lastMessageAt DESC NULLS LAST
    """)
    fun findAllByParticipantId(userId: UserId): List<ConversationEntity>

    @Query("""
        SELECT c 
        FROM ConversationEntity c
        WHERE (c.patient.id = :userId OR c.doctor.id = :userId)
        AND c.id = :conversationId
    """)
    fun findConversationEntityByParticipantId(
        conversationId: ConversationId,
        userId: UserId
    ): ConversationEntity?

}