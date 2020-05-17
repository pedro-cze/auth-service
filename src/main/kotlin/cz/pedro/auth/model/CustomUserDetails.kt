package cz.pedro.auth.model

import org.springframework.security.core.userdetails.UserDetails

interface CustomUserDetails : UserDetails {

    fun username(): String

    fun password(): String

}
