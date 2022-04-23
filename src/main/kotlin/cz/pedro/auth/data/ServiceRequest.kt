package cz.pedro.auth.data

sealed class ServiceRequest(
        open val appId: String?,
        open val username: String?,
        open val password: String?,
        open val authorities: String? = "",
        open val active: Boolean? = false) {

    class SessionRequest(
            override val appId: String,
            override val username: String,
            override val password: String
    ) : ServiceRequest(appId, username, password)

    class AuthenticationRequest(
            override val appId: String,
            override val username: String,
            override val password: String
    ) : ServiceRequest(appId, username, password)

    class RegistrationRequest(
            override val appId: String,
            override val username: String,
            override val password: String,
            override val authorities: String
    ) : ServiceRequest(appId, username, password, authorities)

    class PatchRequest(
            override val username: String?,
            override val password: String?,
            override val authorities: String?,
            override val active: Boolean?
    ) : ServiceRequest(null, username, password, authorities, active)
}
