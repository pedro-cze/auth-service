package cz.pedro.auth.service.impl

import cz.pedro.auth.entity.User
import cz.pedro.auth.error.AuthenticationFailure
import cz.pedro.auth.error.AuthenticationFailure.EmptyUsername
import cz.pedro.auth.error.AuthenticationFailure.Unauthorized
import cz.pedro.auth.error.AuthenticationFailure.UserNotFound
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.service.AuthService
import cz.pedro.auth.service.TokenGenerationService
import cz.pedro.auth.util.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    @Autowired val userRepository: UserRepository,
    @Autowired val tokenGenerationService: TokenGenerationService
) : AuthService {

    override fun login(username: String, password: String): Either<AuthenticationFailure, String> =
            checkUsername(username)
                    .flatMap { loadUser(it) }
                    .flatMap { checkPassword(it, password) }

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

    private fun checkPassword(user: User, password: String): Either<AuthenticationFailure, String> {
        return if (password == user.password) {
            Either.right(generateToken(user))
        } else {
            Either.left(Unauthorized("Unauthorized"))
        }
    }

    private fun generateToken(user: User): String = tokenGenerationService.generateToken(user)
}
