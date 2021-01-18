package cz.pedro.auth.error

sealed class PatchFailure(override val message: String) : GeneralFailure(message) {

    class AppIdPatchFailure(message: String = "Patch not allowed.") : PatchFailure(message)
}
