package cz.pedro.auth.error

sealed class ValidationFailure(override val message: String) : GeneralFailure(message) {

    class InvalidAppId(message: String = "Invalid app id") : ValidationFailure(message)

    class MissingAppId(message: String = "Request app id missing") : ValidationFailure(message)

    class InvalidRequest(message: String = "Request didn't pass validation") : ValidationFailure(message)

    class NullOrEmptyUsername(message: String = "Null or empty username") : ValidationFailure(message)

    class NullOrEmptyPassword(message: String = "Null or empty password") : ValidationFailure(message)
}
