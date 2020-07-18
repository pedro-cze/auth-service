package cz.pedro.auth.util

sealed class Either<L, R> {

    abstract fun <C> map(f: (R) -> C): Either<L, C>

    abstract fun <C> flatMap(f: (R) -> Either<L, C>): Either<L, C>

    abstract fun isLeft(): Boolean

    abstract fun getOrElse(f: () -> R): R

    abstract fun <C> fold(f1: (L) -> C, f2: (R) -> C): C

    class Left<L, R>(private val value: L) : Either<L, R>() {

        override fun <C> map(f: (R) -> C): Either<L, C> = Left(value)

        override fun <C> flatMap(f: (R) -> Either<L, C>): Either<L, C> = Left(value)

        override fun isLeft(): Boolean = true

        override fun toString(): String = value.toString()

        override fun getOrElse(f: () -> R): R = f()

        override fun <C> fold(f1: (L) -> C, f2: (R) -> C): C = f1(value)
    }

    class Right<L, R>(private val value: R) : Either<L, R>() {

        override fun <C> map(f: (R) -> C): Either<L, C> = Right(f(value))

        override fun <C> flatMap(f: (R) -> Either<L, C>): Either<L, C> = f(value)

        override fun isLeft(): Boolean = false

        override fun toString(): String = value.toString()

        override fun getOrElse(f: () -> R): R = value

        override fun <C> fold(f1: (L) -> C, f2: (R) -> C): C = f2(value)
    }

    companion object {

        fun <L, R> left(value: L): Either<L, R> = Left(value)

        fun <L, R> right(value: R): Either<L, R> = Right(value)
    }
}
