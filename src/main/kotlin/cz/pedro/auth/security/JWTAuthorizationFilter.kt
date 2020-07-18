package cz.pedro.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import cz.pedro.auth.security.JwtProperties.Companion.HEADER_STRING
import cz.pedro.auth.security.JwtProperties.Companion.SECRET
import cz.pedro.auth.security.JwtProperties.Companion.TOKEN_PREFIX
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authenticationManager: AuthenticationManager, val service: UserDetailsService) : BasicAuthenticationFilter(authenticationManager) {

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

            val username = JWT.require(Algorithm.HMAC512(SECRET))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, "")).subject

            username?.let {
                if (it.isBlank()) return null
                val user = service.loadUserByUsername(username)
                return UsernamePasswordAuthenticationToken(user, null, user.authorities)
            }
        }
        return null
    }
}