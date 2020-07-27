package cz.pedro.auth.service

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.util.Either
import java.util.UUID

interface LoginService {

    fun login(username: String, password: String): Either<GeneralFailure, String>

    fun register(request: ServiceRequest.RegistrationRequest): Either<GeneralFailure, String>

    fun update(userId: UUID, request: ServiceRequest.PatchRequest): Either<GeneralFailure, String>
}
