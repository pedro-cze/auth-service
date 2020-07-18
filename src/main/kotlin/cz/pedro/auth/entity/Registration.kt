package cz.pedro.auth.entity

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
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        var username: String,
        var password: String,
        @Enumerated(EnumType.STRING)
        var status: RegistrationStatus
)
