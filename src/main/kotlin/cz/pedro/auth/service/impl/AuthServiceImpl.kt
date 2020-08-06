package cz.pedro.auth.service.impl

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.User
import cz.pedro.auth.error.AuthenticationFailure.Unauthorized
import cz.pedro.auth.error.AuthenticationFailure.UserNotFound
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.security.model.AuthRequester
import cz.pedro.auth.service.AuthService
import cz.pedro.auth.service.TokenGenerationService
import cz.pedro.auth.service.ValidationService
import cz.pedro.auth.util.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthServiceImpl(
    @Autowired val userRepository: UserRepository,
    @Autowired val validationService: ValidationService,
    @Autowired val tokenGenerationService: TokenGenerationService,
    @Autowired val encoder: BCryptPasswordEncoder
) : AuthService {

    @Transactional(readOnly = true)
    override fun login(request: ServiceRequest.AuthenticationRequest): Either<GeneralFailure, String> =
            validationService.validate(request)
                    .flatMap { loadUser(it.username!!) }
                    .map { AuthRequester(it) }
                    .flatMap { checkPassword(request.password, it) }

    @Transactional
    override fun register(request: ServiceRequest.RegistrationRequest): Either<GeneralFailure, String> {
        return validationService.validate(request)
                .flatMap { createUser(request) }
                .map { it.username }
    }

    @Transactional
    override fun update(userId: UUID, request: ServiceRequest.PatchRequest): Either<GeneralFailure, String> {
        return validationService.validate(request)
                .flatMap { findUser(userId) }
                .flatMap { patchUser(it, request) }
                .map { it.username }
    }

    private fun loadUser(username: String): Either<GeneralFailure, User> {
        val user = userRepository.findByUsername(username)
        return if (user == null) {
            Either.left(UserNotFound("User not found"))
        } else {
            Either.right(user)
        }
    }

    private fun findUser(userId: UUID): Either<GeneralFailure, User> {
        val user = userRepository.findById(userId)
        return if (user.isPresent) {
            Either.right(user.get())
        } else {
            Either.left(UserNotFound())
        }
    }

    private fun checkPassword(rawPassword: String, user: AuthRequester): Either<GeneralFailure, String> {
        return if (encoder.matches(rawPassword, user.password) && user.isEnabled) {
            Either.right(generateToken(user))
        } else {
            Either.left(Unauthorized())
        }
    }

    private fun generateToken(user: AuthRequester): String = tokenGenerationService.generateToken(user)

    private fun createUser(request: ServiceRequest.RegistrationRequest): Either<GeneralFailure, User> {
        val user = User(null, request.username, encoder.encode(request.password), request.authorities, request.active)
        val res = userRepository.save(user)
        return if (res.id == null) {
            Either.left(RegistrationFailure.SavingFailed())
        } else {
            Either.right(res)
        }
    }

    private fun patchUser(user: User, patch: ServiceRequest.PatchRequest): Either<GeneralFailure, User> {
        val patched = User(
                id = user.id,
                username = patch.username ?: user.username,
                password = patch.password ?: user.password,
                authorities = patch.authorities ?: user.authorities,
                active = patch.active ?: user.active
        )
        val res = userRepository.save(patched)
        return if (res.id != patched.id) {
            Either.left(RegistrationFailure.SavingFailed())
        } else {
            Either.right(res)
        }
    }
}
