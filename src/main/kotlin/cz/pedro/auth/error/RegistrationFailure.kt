package cz.pedro.auth.error

sealed class RegistrationFailure(override val message: String) : GeneralFailure(message) {

    class UsernameAlreadyUsed(message: String = "Username already taken") : RegistrationFailure(message)

    class SavingFailed(message: String = "Something went wrong while trying to save registration") : RegistrationFailure(message)

    class RegistrationNotFound(message: String = "Registration not found") : RegistrationFailure(message)

    class RegistrationExpired(message: String = "Registration expired") : RegistrationFailure(message)

    class RegistrationNotPending(message: String = "Registration not PENDING") : RegistrationFailure(message)

    class UserNotFound(message: String = "Could not activate user") : RegistrationFailure(message)

    class ActiveUserDetected(message: String = "Active user detected. Inconsistent state.") : RegistrationFailure(message)

    class EmailNotSent(message: String = "Some error occurred while sending registration email.") : RegistrationFailure(message)
}
