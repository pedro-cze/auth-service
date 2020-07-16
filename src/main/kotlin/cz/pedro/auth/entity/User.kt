package cz.pedro.auth.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class User(@Id var id: Long, var username: String, var password: String)
