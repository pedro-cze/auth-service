package cz.pedro.auth.error

sealed class RegistrationFailure(override val message: String) : GeneralFailure(message) {

    override fun toString(): String = "Failure: $message"

    class UsernameAlreadyUsed(message: String = "Username already taken") : RegistrationFailure(message)

    class SavingFailed(message: String = "Something went wrong while trying to save registration") : RegistrationFailure(message)
}
