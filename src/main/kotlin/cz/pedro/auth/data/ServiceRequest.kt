package cz.pedro.auth.data

sealed class ServiceRequest(open val username: String?, open val password: String?, open val authorities: String?, open val active: Boolean? = false) {

    class RegistrationRequest(
            override val username: String,
            override val password: String,
            override val authorities: String,
            override val active: Boolean
    ) : ServiceRequest(username, password, authorities, active)

    class PatchRequest(
            override val username: String?,
            override val password: String?,
            override val authorities: String?,
            override val active: Boolean?
    ) : ServiceRequest(username, password, authorities, active)
}
