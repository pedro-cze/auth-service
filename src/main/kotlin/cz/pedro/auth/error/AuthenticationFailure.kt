package cz.pedro.auth.error

sealed class AuthenticationFailure(private val message: String) {

    override fun toString(): String = "Failure: $message"

    class UserNotFound(message: String = "") : AuthenticationFailure(message)

    class EmptyUsername(message: String = "") : AuthenticationFailure(message)

    class Unauthorized(message: String = "") : AuthenticationFailure(message)
}
