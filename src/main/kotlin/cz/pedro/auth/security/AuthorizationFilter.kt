package cz.pedro.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cz.pedro.auth.security.JwtProperties.Companion.HEADER_STRING
import cz.pedro.auth.security.JwtProperties.Companion.SECRET
import cz.pedro.auth.security.JwtProperties.Companion.TOKEN_PREFIX
import cz.pedro.auth.service.AuthorizationService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthorizationFilter(authenticationManager: AuthenticationManager, val service: AuthorizationService) : BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader(HEADER_STRING)
        header?.let {
            if (header.startsWith(TOKEN_PREFIX)) {
                SecurityContextHolder.getContext().authentication = getAuthentication(request)
            }
        }
        chain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token: String? = request.getHeader(HEADER_STRING)
        token?.let {

            val jwtVerified = JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))

            val username = jwtVerified.subject
            val appId = jwtVerified.issuer

            username?.let {
                if (it.isBlank()) return null
                val user: UserDetails = service.loadUserByUsernameAndAppId(username, appId)
                return UsernamePasswordAuthenticationToken(user, null, user.authorities)
            }
        }
        return null
    }
}
