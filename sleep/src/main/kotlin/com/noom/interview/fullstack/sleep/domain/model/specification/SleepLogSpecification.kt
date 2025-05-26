package com.noom.interview.fullstack.sleep.domain.model.specification

import com.noom.interview.fullstack.sleep.domain.model.SleepLog
import com.noom.interview.fullstack.sleep.infrastructure.util.getDateNowByServerMachine
import com.noom.interview.fullstack.sleep.infrastructure.util.getDateThirtyDaysLastByServerMachine
import org.springframework.data.jpa.domain.Specification
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class SleepLogSpecification(val idUser: String, private val isThirtyDaysLast: Boolean = false) : Specification<SleepLog> {
    override fun toPredicate(
        root: Root<SleepLog>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val predicates = mutableListOf<Predicate>()
        val dateSleepPath = root.get<Instant>("dateSleep")

        predicates.add(criteriaBuilder.equal(root.get<String>("idUser"), idUser))

        if(isThirtyDaysLast) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(dateSleepPath, getDateThirtyDaysLastByServerMachine()))
            predicates.add(criteriaBuilder.lessThan(dateSleepPath, getDateNowByServerMachine().plus(1, ChronoUnit.DAYS)))
        }

        return criteriaBuilder.and(*predicates.toTypedArray())
    }
}