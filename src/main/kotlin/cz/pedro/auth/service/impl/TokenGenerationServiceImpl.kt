package cz.pedro.auth.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cz.pedro.auth.security.JwtProperties
import cz.pedro.auth.security.JwtProperties.Companion.TOKEN_PREFIX
import cz.pedro.auth.security.model.AuthRequester
import cz.pedro.auth.service.TokenGenerationService
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

@Service
class TokenGenerationServiceImpl : TokenGenerationService {

    override fun generateToken(user: AuthRequester): String {
        val algorithm = Algorithm.HMAC256(JwtProperties.SECRET)
        val token = JWT.create()
                .withExpiresAt(getExpiration())
                .withIssuer(user.user.serviceName) // TODO
                .withSubject(user.username)
                .sign(algorithm)
        return "$TOKEN_PREFIX$token"
    }

    private fun getExpiration(): Date =
            Date.from(LocalDateTime.now().plusMinutes(JwtProperties.EXPIRATION_TIME).toInstant(ZoneOffset.UTC))
}
