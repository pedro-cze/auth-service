package cz.pedro.auth.configuration

import cz.pedro.auth.security.JWTAuthenticationFilter
import cz.pedro.auth.security.JWTAuthorizationFilter
import cz.pedro.auth.service.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {

        val authFilter = JWTAuthenticationFilter()
        authFilter.setAuthenticationManager(authenticationManager())

        http?.csrf()?.disable()?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)?.and()

                ?.addFilter(authFilter)
                ?.addFilter(JWTAuthorizationFilter(authenticationManager(), userDetailsService()))
                ?.authorizeRequests()
                ?.antMatchers(HttpMethod.POST, "/login")?.permitAll()
                ?.antMatchers(HttpMethod.GET, "/api/public/*")?.hasRole("ADMIN")
                ?.anyRequest()?.authenticated()

    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userDetailsService())?.passwordEncoder(passwordEncoder())
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {
        return UserDetailsServiceImpl()
    }

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }

    @Bean
    fun passwordEncoder() : BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

}
