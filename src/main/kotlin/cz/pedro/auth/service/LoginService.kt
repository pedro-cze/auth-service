package cz.pedro.auth.service

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.error.AuthenticationFailure
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.util.Either

interface LoginService {

    fun login(username: String, password: String): Either<AuthenticationFailure, String>

    fun register(request: ServiceRequest): Either<RegistrationFailure, String>

    fun update(request: ServiceRequest): Either<RegistrationFailure, String>
}
