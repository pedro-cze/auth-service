package cz.pedro.auth.service

import cz.pedro.auth.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    lateinit var repository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        username?.let {
            val appUser = repository.findByUsername(username)
            appUser?.let {
                return User(appUser.username, appUser.password, appUser.rolesAsList())
            }
        }
        throw UsernameNotFoundException(username)
    }
}
