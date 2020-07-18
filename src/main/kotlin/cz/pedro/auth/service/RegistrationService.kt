package cz.pedro.auth.service

import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.util.Either

interface RegistrationService {

    fun register(username: String, password: String): Either<RegistrationFailure, Long>
}
