package cz.pedro.auth.service

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.SessionObject
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.util.Either
import java.util.UUID

interface AuthService {

    fun getSession(request: ServiceRequest.SessionRequest): Either<GeneralFailure, SessionObject>

    fun invalidateSession(sessionId: UUID): Either<GeneralFailure, String>

    fun login(request: ServiceRequest.AuthenticationRequest): Either<GeneralFailure, String>

    fun update(userId: UUID, request: ServiceRequest.PatchRequest): Either<GeneralFailure, String>
}
