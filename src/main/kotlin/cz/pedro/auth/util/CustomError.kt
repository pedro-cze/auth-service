package cz.pedro.auth.util

sealed class CustomError(val message: String, val cause: Exception?) {

    class UserNotFound(message: String, cause: Exception? = null) : CustomError(message, cause)

    class EmptyUsername(message: String, cause: Exception? = null) : CustomError(message, cause)

    class Unauthorized(message: String, cause: Exception? = null) : CustomError(message, cause)

}
