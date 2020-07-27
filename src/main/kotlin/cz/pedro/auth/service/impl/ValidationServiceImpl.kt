package cz.pedro.auth.service.impl

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.ServiceAuthority
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.error.ValidationFailure
import cz.pedro.auth.service.ValidationService
import cz.pedro.auth.util.Either
import org.springframework.stereotype.Service

@Service
class ValidationServiceImpl : ValidationService {

    override fun validate(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return when (request) {
            is ServiceRequest.RegistrationRequest -> registrationStrategy(request)
            else -> patchStrategy(request)
        }
    }

    private fun registrationStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return if (request.authorities == null || request.username == null || request.password == null) {
            Either.left(ValidationFailure.InvalidRequest())
        } else {
            checkAuthoritiesEmpty(request.authorities!!).flatMap { authorities ->
                val res: Boolean = checkAuthoritiesValid(authorities.split(","), true)
                if (res) {
                    Either.right(request)
                } else {
                    Either.left<GeneralFailure, ServiceRequest>(ValidationFailure.InvalidRequest())
                }
            }
        }
    }

    private fun patchStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        TODO("Not implemented")
    }

    private fun checkAuthoritiesEmpty(authorities: String): Either<GeneralFailure, String> {
        return if (authorities.isEmpty()) {
            Either.left(ValidationFailure.InvalidRequest("Authorities are empty")) // Is this valid?
        } else {
            Either.right(authorities)
        }
    }

    private fun checkAuthoritiesValid(authorities: List<String>, result: Boolean): Boolean {
        return if (!result || authorities.isEmpty()) {
            result
        } else {
            val head = authorities.first()
            val tail = authorities.drop(0)
            checkAuthoritiesValid(tail, result && isValidAuthority(head))
        }
    }

    private fun isValidAuthority(token: String): Boolean {
        return ServiceAuthority.ADMIN.name == token || ServiceAuthority.USER.name == token
    }
}
