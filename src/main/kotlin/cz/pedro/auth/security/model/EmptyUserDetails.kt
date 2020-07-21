package cz.pedro.auth.security.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class EmptyUserDetails : UserDetails {

    override fun getAuthorities(): List<GrantedAuthority> = emptyList()

    override fun isEnabled(): Boolean = false

    override fun getUsername(): String = ""

    override fun isCredentialsNonExpired(): Boolean = false

    override fun getPassword(): String = ""

    override fun isAccountNonExpired(): Boolean = false

    override fun isAccountNonLocked(): Boolean = false
}
