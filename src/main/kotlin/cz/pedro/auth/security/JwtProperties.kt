package cz.pedro.auth.security

class JwtProperties {

    companion object {
        const val EXPIRATION_TIME = 10L
        const val SECRET = "4=a^sjzA/-n49m^ZP(@ad>H"
        const val HEADER_STRING = "Authentication"
        const val TOKEN_PREFIX = "Bearer "
    }

}
