package cz.pedro.auth.service

import cz.pedro.auth.entity.RegistrationStatus
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.util.Either
import java.util.UUID

interface RegistrationService {

    fun register(username: String, password: String): Either<RegistrationFailure, UUID>

    fun update(registrationId: UUID, status: RegistrationStatus): Either<RegistrationFailure, UUID>
}
