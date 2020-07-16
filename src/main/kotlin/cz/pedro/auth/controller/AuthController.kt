package cz.pedro.auth.controller

import cz.pedro.auth.data.LoginRequest
import cz.pedro.auth.data.LoginResponse
import cz.pedro.auth.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/auth"])
class AuthController(@Autowired val authService: AuthService, @Autowired val encoder: BCryptPasswordEncoder) {

    @PostMapping(path = ["/login"])
    fun auth(loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val encodedPassword = encoder.encode(loginRequest.password)
        val res = authService.login(loginRequest.username, encodedPassword)
        return when (res.isLeft()) {
            true -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            else -> ResponseEntity.ok(LoginResponse(res.toString()))
        }
    }

}
