package cz.pedro.auth.service.impl

import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.security.model.AuthRequester
import cz.pedro.auth.security.model.EmptyUserDetails
import cz.pedro.auth.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl : AuthenticationService {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        username?.let {
            userRepository.findByUsername(it)?.let {
                return AuthRequester(it)
            }
        }
        return EmptyUserDetails()
    }

    override fun loadUserByUsernameAndAppId(username: String?, appId: String): UserDetails {
        username?.let {
            userRepository.findByUsernameAndServiceName(username, appId)?.let {
                return AuthRequester(it)
            }
        }
        return EmptyUserDetails()
    }
}
