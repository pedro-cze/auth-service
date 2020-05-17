package cz.pedro.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import cz.pedro.auth.data.UserDto
import cz.pedro.auth.model.AppUser
import cz.pedro.auth.security.JwtProperties.Companion.EXPIRATION_TIME
import cz.pedro.auth.security.JwtProperties.Companion.HEADER_STRING
import cz.pedro.auth.security.JwtProperties.Companion.SECRET
import cz.pedro.auth.security.JwtProperties.Companion.TOKEN_PREFIX
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        try {
            val userDto = ObjectMapper().readValue(request?.inputStream, UserDto::class.java)
            return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(userDto.username, userDto.password))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authResult: Authentication?) {

        val token = JWT.create()
                .withSubject((authResult?.principal as User).username)
                .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(EXPIRATION_TIME).atZone(ZoneId.systemDefault()).toInstant()))
                .sign(Algorithm.HMAC512(SECRET))

        response?.addHeader(HEADER_STRING, "$TOKEN_PREFIX$token")
    }
}
