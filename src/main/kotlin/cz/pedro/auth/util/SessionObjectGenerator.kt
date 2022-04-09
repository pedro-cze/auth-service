package cz.pedro.auth.util

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.SessionObject
import cz.pedro.auth.error.GeneralFailure

interface SessionObjectGenerator {

    fun generateSessionObject(sessionRequest: ServiceRequest): Either<GeneralFailure, SessionObject>
}
