package cz.pedro.auth.error

abstract class GeneralFailure(open val message: String) {
    override fun toString(): String = "Failure: $message"
}
