package cz.pedro.auth.service

import cz.pedro.auth.error.SessionObjectFailure
import cz.pedro.auth.util.Either

interface SessionService {

    fun validateSession(sessionHash: String): Either<SessionObjectFailure.SessionObjectNotFound, String>

    fun invalidateSession(sessionHash: String): Either<SessionObjectFailure, String>
}
