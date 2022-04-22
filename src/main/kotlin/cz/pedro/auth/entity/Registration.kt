package cz.pedro.auth.entity

import java.time.Instant
import java.util.Date
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.Enumerated
import javax.persistence.EnumType
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
@Table(name = "AUTH_REGISTRATION")
data class Registration(
        @Id
        var id: UUID? = UUID.randomUUID(),
        var username: String,
        var password: String,
        var hash: String,
        @Enumerated(EnumType.STRING)
        var status: RegistrationStatus = RegistrationStatus.PENDING,
        @Temporal(TemporalType.TIMESTAMP)
        var created: Date = Date.from(Instant.now())
)
