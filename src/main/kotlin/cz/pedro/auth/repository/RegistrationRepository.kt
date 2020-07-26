package cz.pedro.auth.repository

import cz.pedro.auth.entity.Registration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RegistrationRepository : JpaRepository<Registration, UUID> {
    fun findByUsername(username: String): Registration?
}
