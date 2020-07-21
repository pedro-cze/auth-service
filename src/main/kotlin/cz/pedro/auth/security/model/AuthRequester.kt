package cz.pedro.auth.security.model

import cz.pedro.auth.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

class AuthRequester(val user: User) : UserDetails {

    override fun getAuthorities(): List<GrantedAuthority> =
            user.authorities
                    .split(",")
                    .stream()
                    .map { role -> GrantedAuthority { "ROLE_$role" } }
                    ?.collect(Collectors.toList()) ?: emptyList()

    override fun isEnabled(): Boolean = true

    override fun getUsername(): String = user.username

    override fun isCredentialsNonExpired(): Boolean = true

    override fun getPassword(): String = user.password

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true
}
