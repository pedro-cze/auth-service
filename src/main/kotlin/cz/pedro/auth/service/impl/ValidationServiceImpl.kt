package cz.pedro.auth.service.impl

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.error.ValidationFailure
import cz.pedro.auth.service.ValidationService
import cz.pedro.auth.util.Either
import org.springframework.stereotype.Service

@Service
class ValidationServiceImpl : ValidationService {

    override fun validate(request: ServiceRequest): Either<ValidationFailure, ServiceRequest> {
        return when(request) {
            is ServiceRequest.RegistrationRequest -> registrationStrategy()
            else -> patchStrategy()
        }
    }

    private fun registrationStrategy(): Either<ValidationFailure, ServiceRequest> {
        TODO("Not implemented")
    }

    private fun patchStrategy(): Either<ValidationFailure, ServiceRequest> {
        TODO("Not implemented")
    }
}
