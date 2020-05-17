package cz.pedro.auth.data

import com.fasterxml.jackson.annotation.JsonProperty
import cz.pedro.auth.model.AppUser

data class UserDto(
        @JsonProperty("id")
        val id: Long,
        @JsonProperty("username")
        val username: String,
        @JsonProperty("password")
        val password: String,
        @JsonProperty("roles")
        val roles: String?,
        @JsonProperty("permissions")
        val permissions: String?
): Dto {

    override fun toModel(): AppUser {

        TODO("Not yet implemented")
    }
}
