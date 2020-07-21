package cz.pedro.auth.data

data class RegistrationRequest(val username: String, val password: String, val authorities: String)
