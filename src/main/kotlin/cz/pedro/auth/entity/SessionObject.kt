package cz.pedro.auth.entity

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "session_object")
data class SessionObject(
        @Id
        val sessionId: UUID,
        val username: String,
        val created: LocalDateTime,
        val appId: String
)
