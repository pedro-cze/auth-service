package cz.pedro.auth.service

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.util.Either

interface RegistrationService {

    fun register(request: ServiceRequest.RegistrationRequest): Either<GeneralFailure, String>

    fun confirmRegistration(hash: String): Either<GeneralFailure, String>
}
