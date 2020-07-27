package cz.pedro.auth.error

sealed class RegistrationFailure(private val message: String) {

    override fun toString(): String = "Failure: $message"

    class UsernameAlreadyUsed(message: String = "Username already taken") : RegistrationFailure(message)

    class SavingFailed(message: String = "Something went wrong while trying to save registration") : RegistrationFailure(message)

    class InvalidRequest(message: String = "Request didn't pass validation"): RegistrationFailure(message)

}
