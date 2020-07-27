package cz.pedro.auth.error

sealed class RegistrationFailure(private val message: String) {

    override fun toString(): String = "Failure: $message"

    class UsernameAlreadyUsed(message: String = "Username already taken") : RegistrationFailure(message)

    class SavingFailed(message: String = "Something went wrong while trying to save registration") : RegistrationFailure(message)

    class PendingRegistration(message: String = "Pending registration") : RegistrationFailure(message)

    class RegistrationNotFound(message: String = "Registration not found") : RegistrationFailure(message)

    class UnexpectedStatus(message: String = "Wrong registration status") : RegistrationFailure(message)
}
