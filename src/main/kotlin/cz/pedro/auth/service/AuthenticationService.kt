package cz.pedro.auth.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

interface AuthenticationService : UserDetailsService {

    fun loadUserByUsernameAndAppId(username: String?, appId: String): UserDetails
}
