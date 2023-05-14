package cz.pedro.auth.data

import java.util.UUID

data class UserDto(
    val id: UUID,
    val serviceName: String,
    val userName: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val password: String,
    val authorities: String,
    val active: Boolean
)
