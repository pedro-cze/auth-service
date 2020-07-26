package cz.pedro.auth.service

import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.util.Either
import java.util.*

interface RegistrationService {

    fun register(username: String, password: String): Either<RegistrationFailure, UUID>

    fun confirm(registrationId: UUID): Either<RegistrationFailure, UUID>
}
