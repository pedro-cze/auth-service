package cz.pedro.auth.service

import cz.pedro.auth.util.CustomError
import cz.pedro.auth.util.Either
import org.springframework.stereotype.Service

@Service
interface AuthService {

    fun login(username: String, password: String): Either<CustomError, String>
}
