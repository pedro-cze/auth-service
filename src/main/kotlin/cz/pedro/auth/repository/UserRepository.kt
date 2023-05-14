package cz.pedro.auth.repository

import cz.pedro.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    fun findByUsername(username: String): User?

    fun findByUsernameAndServiceName(username: String, serviceName: String): User?

    fun findByServiceName(serviceName: String): List<User>
}
