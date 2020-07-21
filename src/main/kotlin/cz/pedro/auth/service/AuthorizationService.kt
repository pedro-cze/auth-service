package cz.pedro.auth.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
interface AuthorizationService : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails
}
