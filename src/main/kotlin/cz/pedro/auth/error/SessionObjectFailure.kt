package cz.pedro.auth.error

sealed class SessionObjectFailure(override val message: String) : GeneralFailure(message) {

    class SessionHashGenerationFailure(message: String = "Session hash generation failed") : SessionObjectFailure(message)
    class SessionObjectGenerationFailure(message: String = "Session object generation failed") : SessionObjectFailure(message)
    class SessionSaveFailure(message: String = "Saving session failed") : SessionObjectFailure(message)
    class SessionObjectNotFound(message: String = "Session object with given id not found.") : SessionObjectFailure(message)
}
