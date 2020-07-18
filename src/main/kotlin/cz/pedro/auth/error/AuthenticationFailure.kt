package cz.pedro.auth.error

sealed class AuthenticationFailure(val message: String, val cause: Exception?) {

    class UserNotFound(message: String, cause: Exception? = null) : AuthenticationFailure(message, cause)

    class EmptyUsername(message: String, cause: Exception? = null) : AuthenticationFailure(message, cause)

    class Unauthorized(message: String, cause: Exception? = null) : AuthenticationFailure(message, cause)
}
