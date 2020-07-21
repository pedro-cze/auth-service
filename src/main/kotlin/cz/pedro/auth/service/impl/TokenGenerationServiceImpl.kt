package cz.pedro.auth.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cz.pedro.auth.security.model.AuthRequester
import cz.pedro.auth.service.TokenGenerationService
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

@Service
class TokenGenerationServiceImpl : TokenGenerationService {

    override fun generateToken(user: AuthRequester): String {
        val algorithm = Algorithm.HMAC256("secret_salt")
        return JWT.create()
                .withExpiresAt(getExpiration())
                .withSubject(user.username)
                .sign(algorithm)
    }

    private fun getExpiration(): Date =
            Date.from(LocalDateTime.now().plusMinutes(10L).toInstant(ZoneOffset.UTC))
}
