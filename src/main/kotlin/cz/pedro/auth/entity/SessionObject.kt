package cz.pedro.auth.entity

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "session_object")
data class SessionObject(
        @Id
        val sessionId: UUID,
        val username: String,
        @Temporal(TemporalType.TIMESTAMP)
        val expires: Date,
        val appId: String
)
