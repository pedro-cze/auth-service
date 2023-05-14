package cz.pedro.auth.util

sealed class AppConstants {

    object Authority {
        const val USER: String = "USER"
        const val ADMIN: String = "ADMIN"
    }

    object Misc {
        const val EMPTY_STRING: String = ""
    }
}
