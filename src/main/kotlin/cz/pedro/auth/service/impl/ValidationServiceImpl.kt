package cz.pedro.auth.service.impl

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.error.ValidationFailure
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.error.MissingAppId
import cz.pedro.auth.error.PatchFailure
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.service.ValidationService
import cz.pedro.auth.service.impl.ValidationServiceImpl.Constants.DEFAULT_SYSTEM_ROLES
import cz.pedro.auth.util.AppConstants
import cz.pedro.auth.util.AppId
import cz.pedro.auth.util.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ValidationServiceImpl(
        @Autowired val userRepository: UserRepository
) : ValidationService {

    object Constants {
        val DEFAULT_SYSTEM_ROLES = listOf(AppConstants.Authority.USER, AppConstants.Authority.ADMIN)
    }

    override fun validate(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return when (request) {
            is ServiceRequest.SessionRequest -> sessionStrategy(request)
            is ServiceRequest.AuthenticationRequest -> authenticationStrategy(request)
            is ServiceRequest.RegistrationRequest -> registrationStrategy(request)
            else -> patchStrategy(request)
        }
    }

    private fun sessionStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return checkAppIdIsNotNull(request)
                .flatMap { checkAppIdExists(it) }
                .flatMap { checkUsernameNotEmpty(it, "Null or empty username") }
    }

    private fun authenticationStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return checkUsernameNotEmpty(request, "Null or empty username")
            .flatMap { checkAppIdIsNotNull(request) }
            .flatMap { checkAppIdExists(request) }
    }

    private fun registrationStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return checkUsernameNotEmpty(request, "Null or empty username")
                .flatMap { checkAppIdExists(request) }
                .flatMap { checkPasswordNotEmptyOrNull(request, "Null or empty password") }
                .flatMap { checkAuthoritiesValid(request) }
                .flatMap { checkUsernameNotTaken(request) }
    }

    private fun patchStrategy(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return checkUsernameIfPresent(request)
                .flatMap { checkPasswordIfPresent(request) }
                .flatMap { checkAuthoritiesIfPresent(request) }
                .flatMap { checkAppId(request) }
                .flatMap { checkUsernameNotTakenPatch(request) }
    }

    private fun checkAppIdIsNotNull(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        return if (request.appId == null) {
            Either.left(ValidationFailure.MissingAppId())
        } else {
            Either.right(request)
        }
    }

    private fun checkAppIdExists(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        val appId: String = request.appId ?: return Either.left(ValidationFailure.MissingAppId())
        if (AppId.values().find { it.name == appId } == null) {
            return Either.left(ValidationFailure.InvalidAppId())
        }
        return Either.right(request)
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
        return DEFAULT_SYSTEM_ROLES.contains(token)
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
        request.username?.let { username ->
            request.appId?.let { appId ->
                val user = userRepository.findByUsernameAndServiceName(username, appId)
                user?.let {
                    return Either.left(RegistrationFailure.UsernameAlreadyUsed())
                }
                return Either.right(request)
            }
            return Either.left(MissingAppId())
        }
        return Either.left(ValidationFailure.InvalidRequest("Missing username."))
    }

    private fun checkUsernameNotTakenPatch(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        request.username?.let { username ->
            val user = userRepository.findByUsername(username)
            user?.let {
                return Either.left(RegistrationFailure.UsernameAlreadyUsed())
            }
            return Either.right(request)
        }
        return Either.right(request)
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

    private fun checkAppId(request: ServiceRequest): Either<GeneralFailure, ServiceRequest> {
        request.appId?.let {
            return Either.left(PatchFailure.AppIdPatchFailure())
        }
        return Either.right(request)
    }
}
