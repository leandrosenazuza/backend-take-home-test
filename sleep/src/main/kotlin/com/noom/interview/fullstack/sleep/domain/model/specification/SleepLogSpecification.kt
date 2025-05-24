package com.noom.interview.fullstack.sleep.domain.model.specification

import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import org.springframework.data.jpa.domain.Specification
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class SleepLogSpecification(val idUser: String, val isThirtyDaysLast: Boolean = false) : Specification<SleepLog> {
    override fun toPredicate(
        root: Root<SleepLog>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val zoneId = ZoneId.systemDefault()
        val predicates = mutableListOf<Predicate>()
        val today = if(isThirtyDaysLast) LocalDate.now().minusDays(30) else LocalDate.now()
        val startOfDayInstant = today.atStartOfDay(zoneId).toInstant()
        val startOfNextDayInstant = today.plusDays(1).atStartOfDay(zoneId).toInstant()
        val dateSleepPath = root.get<Instant>("dateSleep")

        predicates.add(criteriaBuilder.equal(root.get<String>("idUser"), idUser))
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(dateSleepPath, startOfDayInstant))
        predicates.add(criteriaBuilder.lessThan(dateSleepPath, startOfNextDayInstant))

        return criteriaBuilder.and(*predicates.toTypedArray())
    }
}