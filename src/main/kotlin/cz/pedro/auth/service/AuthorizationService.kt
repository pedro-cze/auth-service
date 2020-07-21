package cz.pedro.auth.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

interface AuthorizationService : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails
}
