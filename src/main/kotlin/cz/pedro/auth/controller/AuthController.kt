package cz.pedro.auth.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/auth"])
class AuthController {

    @PostMapping(path = ["/login"])
    fun auth() {
        TODO()
    }

}
