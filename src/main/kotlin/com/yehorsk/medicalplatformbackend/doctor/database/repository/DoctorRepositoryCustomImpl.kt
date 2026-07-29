package com.yehorsk.medicalplatformbackend.doctor.database.repository

import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import com.yehorsk.medicalplatformbackend.doctor.database.entity.WorkplaceEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.query.QueryUtils

class DoctorRepositoryCustomImpl(
    @PersistenceContext private val entityManager: EntityManager
): DoctorRepositoryCustom {

    override fun findAllSliced(
        spec: Specification<DoctorEntity>,
        pageable: Pageable
    ): Slice<DoctorEntity> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(DoctorEntity::class.java)
        val root = query.from(DoctorEntity::class.java)

        spec.toPredicate(root, query, cb)?.let { query.where(it) }

        if (pageable.sort.isSorted) {
            query.orderBy(QueryUtils.toOrders(pageable.sort, root, cb))
        }

        val entityGraph = entityManager.createEntityGraph(DoctorEntity::class.java).apply {
            addAttributeNodes("user", "specialization")
            addSubgraph<WorkplaceEntity>("workplace").addAttributeNodes("clinic")
        }

        val results = entityManager.createQuery(query)
            .setFirstResult(pageable.offset.toInt())
            .setMaxResults(pageable.pageSize + 1)
            .setHint("jakarta.persistence.fetchgraph", entityGraph)
            .resultList

        val hasNext = results.size > pageable.pageSize
        val content = if (hasNext) results.dropLast(1) else results

        return SliceImpl(content, pageable, hasNext)
    }

}