package cz.pedro.auth.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "AUTH_REGISTRATION")
data class Registration(
        @Id
        var id: UUID? = UUID.randomUUID(),
        var username: String,
        var password: String,
        @Enumerated(EnumType.STRING)
        var status: RegistrationStatus
)
