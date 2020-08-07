package cz.pedro.auth.service.impl

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.ServiceAuthority
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
        return checkUsernameNotEmpty(request, "Null or empty username")
    }

    private fun registrationStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return checkUsernameNotEmpty(request, "Null or empty username")
                .flatMap { checkPasswordNotEmptyOrNull(request, "Null or empty password") }
                .flatMap { checkAuthoritiesValid(request) }
                .flatMap { checkUsernameNotTaken(request) }
    }

    private fun patchStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return checkUsernameIfPresent(request)
                .flatMap { checkPasswordIfPresent(request) }
                .flatMap { checkAuthoritiesIfPresent(request) }
                .flatMap { checkUsernameNotTakenIfPresent(request) }
    }

    private fun checkAuthoritiesValid(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return if (request.authorities == null) {
            Either.left(ValidationFailure.InvalidRequest())
        } else {
            val res: Boolean = validateAuthorities(request.authorities!!.split(Regex(",\\s?")).filter { it.isNotBlank() }, true)
            if (res) {
                Either.right(request)
            } else {
                Either.left<GeneralFailure, ServiceRequest>(ValidationFailure.InvalidRequest())
            }
        }
    }

    private fun checkAuthoritiesIfPresent(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return if (request.authorities == null) {
            Either.right(request)
        } else {
            checkAuthoritiesValid(request)
        }
    }

    private fun validateAuthorities(authorities: List<String>, result: Boolean): Boolean {
        if (!result || authorities.isEmpty()) {
            return result
        }
        val head = authorities.first()
        val tail = authorities.drop(1)
        return validateAuthorities(tail, result && isValidAuthority(head))
    }

    private fun isValidAuthority(token: String): Boolean {
        return ServiceAuthority.ADMIN.name == token || ServiceAuthority.USER.name == token
    }

    private fun checkUsernameNotEmpty(request: ServiceRequest, errorMessage: String): Either<GeneralFailure, ServiceRequest> {
        return if (request.username != null && request.username!!.isNotBlank()) {
            Either.right(request)
        } else {
            Either.left(ValidationFailure.NullOrEmptyUsername(errorMessage))
        }
    }

    private fun checkUsernameIfPresent(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return if (request.username == null) {
            Either.right(request)
        } else {
            checkUsernameNotEmpty(request, "Empty username")
        }
    }

    private fun checkUsernameNotTaken(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        if (request.username != null) {
            if (userRepository.findByUsername(request.username!!) != null) {
                return Either.left(RegistrationFailure.UsernameAlreadyUsed()) // TODO is checked also during patch
            }
        }
        return Either.right(request)
    }

    private fun checkUsernameNotTakenIfPresent(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return if (request.username == null) {
            Either.right(request)
        } else {
            checkUsernameNotTaken(request)
        }
    }

    private fun checkPasswordNotEmptyOrNull(request: ServiceRequest, errorMessage: String): Either<GeneralFailure, ServiceRequest> {
        return if (request.password != null && request.password!!.isNotBlank()) {
            Either.right(request)
        } else {
            Either.left(ValidationFailure.NullOrEmptyPassword(errorMessage))
        }
    }

    private fun checkPasswordIfPresent(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return if (request.password == null) {
            Either.right(request)
        } else {
            checkPasswordNotEmptyOrNull(request, "Empty password")
        }
    }
}
