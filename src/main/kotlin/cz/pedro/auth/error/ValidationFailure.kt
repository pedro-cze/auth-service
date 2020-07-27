package cz.pedro.auth.error

sealed class ValidationFailure(override val message: String) : GeneralFailure(message) {

    class InvalidRequest(message: String = "Request didn't pass validation"): ValidationFailure(message)
}
