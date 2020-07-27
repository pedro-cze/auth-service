package cz.pedro.auth.entity

import javax.persistence.Entity
import javax.persistence.GenerationType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "AUTH_USER")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long,
    var username: String,
    var password: String,
    var authorities: String,
    var active: Boolean
)
