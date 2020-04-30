package cz.pedro.auth.controller

import cz.pedro.auth.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/auth"])
class AuthController(@Autowired val authService: AuthService) {

    @PostMapping(path = ["/login"])
    fun auth() {
        authService.login()
    }

}
