package cz.pedro.auth.entity

import javax.persistence.*

@Entity
@Table(name = "AUTH_USER")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long,
        var username: String,
        var password: String
)
