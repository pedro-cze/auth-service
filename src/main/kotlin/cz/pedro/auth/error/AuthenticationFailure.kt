package cz.pedro.auth.error

sealed class AuthenticationFailure(override val message: String) : GeneralFailure(message) {

    class UserNotFound(message: String = "User not found") : AuthenticationFailure(message)

    class EmptyUsername(message: String = "Empty username") : AuthenticationFailure(message)

    class Unauthorized(message: String = "Unauthorized") : AuthenticationFailure(message)
}
