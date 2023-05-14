package cz.pedro.auth.data

import cz.pedro.auth.util.AppConstants.Misc.EMPTY_STRING

sealed class ServiceRequest(
        open val appId: String?,
        open val username: String?,
        open val password: String?,
        open val authorities: String? = EMPTY_STRING,
        open val firstname: String? = EMPTY_STRING,
        open val lastname: String? = EMPTY_STRING,
        open val email: String? = EMPTY_STRING,
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
            override val firstname: String?,
            override val lastname: String?,
            override val active: Boolean?

    ) : ServiceRequest(null, username, password, authorities, firstname, lastname, null, active)
}
