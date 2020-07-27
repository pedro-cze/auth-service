package cz.pedro.auth.service

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.util.Either

interface ValidationService {

    fun validate(request: ServiceRequest): Either<GeneralFailure, ServiceRequest>
}
