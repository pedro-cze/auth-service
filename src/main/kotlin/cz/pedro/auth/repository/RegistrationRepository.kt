package cz.pedro.auth.repository

import cz.pedro.auth.entity.Registration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegistrationRepository : JpaRepository<Registration, Long> {
    fun findByUsername(username: String): Registration?
}
