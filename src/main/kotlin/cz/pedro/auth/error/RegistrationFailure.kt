package cz.pedro.auth.error

sealed class RegistrationFailure {

    class UsernameAlreadyUsed(val message: String = "") : RegistrationFailure() {
        override fun toString(): String = "Failure: $message"
    }

    class SavingFailed(val message: String = "") : RegistrationFailure() {
        override fun toString(): String = "Failure: $message"
    }

    class PendingRegistration(val message: String = "") : RegistrationFailure() {
        override fun toString(): String = "Failure: $message"
    }
}
