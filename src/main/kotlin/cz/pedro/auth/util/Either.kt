package cz.pedro.auth.util

sealed class Either<L, R> {

    abstract fun <C> map(f: (R) -> C): C

    abstract fun <C> flatMap(f: (R) -> Either<L, C>): Either<L, C>

    abstract fun isLeft(): Boolean

    class Left<L, R>(val value: L) : Either<L, R>() {

        override fun <C> map(f: (R) -> C): C {
            TODO("Not yet implemented")
        }

        override fun <C> flatMap(f: (R) -> Either<L, C>): Either<L, C> {
            TODO("Not yet implemented")
        }

        override fun isLeft(): Boolean = true

        override fun toString(): String = value.toString()
    }

    class Right<L, R>(val value: R) : Either<L, R>() {

        override fun <C> map(f: (R) -> C): C {
            TODO("Not yet implemented")
        }

        override fun <C> flatMap(f: (R) -> Either<L, C>): Either<L, C> {
            TODO("Not yet implemented")
        }

        override fun isLeft(): Boolean = false

        override fun toString(): String = value.toString()
    }

    companion object {

        fun <L, R> left(value: L): Either<L, R> = Left(value)

        fun <L, R> right(value: R): Either<L, R> = Right(value)
    }

}
