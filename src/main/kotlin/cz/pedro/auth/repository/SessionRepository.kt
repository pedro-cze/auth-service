package cz.pedro.auth.repository

import cz.pedro.auth.entity.SessionObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SessionRepository : JpaRepository<SessionObject, UUID>
