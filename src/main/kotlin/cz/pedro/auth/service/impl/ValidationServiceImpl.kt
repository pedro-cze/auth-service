package cz.pedro.auth.service.impl

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.ServiceAuthority
import cz.pedro.auth.entity.User
import cz.pedro.auth.error.AuthenticationFailure
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.error.ValidationFailure
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.service.ValidationService
import cz.pedro.auth.util.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ValidationServiceImpl(
        @Autowired val userRepository: UserRepository
) : ValidationService {

    override fun validate(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return when (request) {
            is ServiceRequest.AuthenticationRequest -> authenticationStrategy(request)
            is ServiceRequest.RegistrationRequest -> registrationStrategy(request)
            else -> patchStrategy(request)
        }
    }

    private fun authenticationStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return checkUsernameNotEmpty(request)
    }

    private fun registrationStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return checkUsernameNotEmpty(request)
                .flatMap { checkAuthoritiesValid(request) }
                .flatMap { checkUsernameNotTaken(request) }
    }

    private fun patchStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return checkUsernameNotTaken(request)
    }

    private fun checkAuthoritiesValid(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return if (request.authorities == null || request.username == null || request.password == null) {
            Either.left(ValidationFailure.InvalidRequest())
        } else {
            val res: Boolean = validateAuthorities(request.authorities!!.split(","), true)
            if (res) {
                Either.right(request)
            } else {
                Either.left<GeneralFailure, ServiceRequest>(ValidationFailure.InvalidRequest())
            }
        }
    }

    private fun validateAuthorities(authorities: List<String>, result: Boolean): Boolean {
        return if (!result || authorities.isEmpty()) {
            result
        } else {
            val head = authorities.first()
            val tail = authorities.drop(0)
            validateAuthorities(tail, result && isValidAuthority(head))
        }
    }

    private fun isValidAuthority(token: String): Boolean {
        return ServiceAuthority.ADMIN.name == token || ServiceAuthority.USER.name == token
    }

    private fun checkUsernameNotEmpty(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return if (request.username == null || request.username!!.isEmpty()) {
            Either.left(ValidationFailure.NullOrEmptyUsername())
        } else {
            Either.right(request)
        }
    }

    private fun checkUsernameNotTaken(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        if (request.username != null) {
            if (userRepository.findByUsername(request.username!!) != null) {
                return Either.left(RegistrationFailure.UsernameAlreadyUsed())
            }
        }
        return Either.right(request)
    }
}
