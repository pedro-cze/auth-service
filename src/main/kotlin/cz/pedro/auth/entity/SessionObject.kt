package cz.pedro.auth.entity

import java.util.Date
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
@Table(name = "session_object")
data class SessionObject(
        @Id
        val sessionId: UUID,
        val username: String,
        @Temporal(TemporalType.TIMESTAMP)
        val expires: Date,
        val appId: String,
        val hash: String
)
