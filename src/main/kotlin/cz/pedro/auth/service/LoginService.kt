package cz.pedro.auth.service

import cz.pedro.auth.error.AuthenticationFailure
import cz.pedro.auth.util.Either
import org.springframework.stereotype.Service

interface LoginService {

    fun login(username: String, password: String): Either<AuthenticationFailure, String>
}
