package cz.pedro.auth.configuration

import cz.pedro.auth.security.AuthorizationFilter
import cz.pedro.auth.service.AuthorizationService
import cz.pedro.auth.service.impl.AuthorizationServiceImpl
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
import java.security.SecureRandom

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http?.cors()?.and()?.csrf()?.disable()?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)?.and()
                ?.addFilter(AuthorizationFilter(authenticationManager(), userDetailsService() as AuthorizationService)) // only certain users can call following endpoints
                ?.authorizeRequests()
                ?.antMatchers(HttpMethod.POST, "/auth/login")?.permitAll()
                ?.antMatchers(HttpMethod.POST, "/session/login")?.permitAll()
                ?.antMatchers(HttpMethod.POST, "/session/valid")?.permitAll()
                ?.antMatchers(HttpMethod.DELETE, "/session/invalidate/*")?.permitAll()
                ?.antMatchers(HttpMethod.GET, "/actuator/health")?.permitAll()
                ?.antMatchers(HttpMethod.POST, "/auth/new")?.permitAll()
                ?.antMatchers(HttpMethod.PATCH, "/admin/update/*")?.hasRole("ADMIN")
                ?.anyRequest()?.authenticated()
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userDetailsService())?.passwordEncoder(passwordEncoder())
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {
        return AuthorizationServiceImpl()
    }

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.`$2A`, 8, SecureRandom.getInstance("SHA1PRNG"))
    }
}
