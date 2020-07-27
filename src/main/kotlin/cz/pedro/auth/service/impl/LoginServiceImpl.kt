package cz.pedro.auth.service.impl

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.User
import cz.pedro.auth.error.AuthenticationFailure
import cz.pedro.auth.error.AuthenticationFailure.EmptyUsername
import cz.pedro.auth.error.AuthenticationFailure.Unauthorized
import cz.pedro.auth.error.AuthenticationFailure.UserNotFound
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.security.model.AuthRequester
import cz.pedro.auth.service.LoginService
import cz.pedro.auth.service.TokenGenerationService
import cz.pedro.auth.service.ValidationService
import cz.pedro.auth.util.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginServiceImpl(
    @Autowired val userRepository: UserRepository,
    @Autowired val validationService: ValidationService,
    @Autowired val tokenGenerationService: TokenGenerationService,
    @Autowired val encoder: BCryptPasswordEncoder
) : LoginService {

    override fun login(username: String, password: String): Either<AuthenticationFailure, String> =
            checkUsername(username)
                    .flatMap { loadUser(it) }
                    .map { AuthRequester(it) }
                    .flatMap { checkPassword(it, password) }

    override fun register(request: ServiceRequest): Either<RegistrationFailure, String> {
        // validate request
        if (validationService.validate(request).isLeft()) {
            return Either.left(RegistrationFailure.InvalidRequest())
        }
        // check for username already taken
        checkUsername(request.username!!)
                .flatMap { createUser(request) }
                .map { it.username }
    }

    override fun update(request: ServiceRequest): Either<RegistrationFailure, String> {
        // validate request
        // update user
        TODO("Not yet implemented")
    }

    private fun checkUsername(username: String): Either<AuthenticationFailure, String> {
        return if (username.isEmpty()) {
            Either.left(EmptyUsername("Empty username"))
        } else {
            Either.right(username)
        }
    }

    private fun loadUser(username: String): Either<AuthenticationFailure, User> {
        val user = userRepository.findByUsername(username)
        return if (user == null) {
            Either.left(UserNotFound("User not found"))
        } else {
            Either.right(user)
        }
    }

    private fun checkPassword(user: AuthRequester, password: String): Either<AuthenticationFailure, String> {
        return if (encoder.matches(password, user.password) && user.isEnabled) {
            Either.right(generateToken(user))
        } else {
            Either.left(Unauthorized("Unauthorized"))
        }
    }

    private fun generateToken(user: AuthRequester): String = tokenGenerationService.generateToken(user)

    private fun createUser(request: ServiceRequest.RegistrationRequest): Either<RegistrationFailure, User> {
        val user = User(null, request.username, request.password, request.authorities, request.active)
        val res = userRepository.save(user)
        return if (res.id == null) {
            Either.left(RegistrationFailure.SavingFailed())
        } else {
            Either.right(res)
        }
    }
}
