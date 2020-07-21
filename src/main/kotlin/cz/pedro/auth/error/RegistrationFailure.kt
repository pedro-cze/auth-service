package cz.pedro.auth.error

sealed class RegistrationFailure(private val message: String) {

    override fun toString(): String = "Failure: $message"

    class UsernameAlreadyUsed(message: String = "") : RegistrationFailure(message)

    class SavingFailed(message: String = "") : RegistrationFailure(message)

    class PendingRegistration(message: String = "") : RegistrationFailure(message)
}
