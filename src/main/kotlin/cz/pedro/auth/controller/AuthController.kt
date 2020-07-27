package cz.pedro.auth.controller

import cz.pedro.auth.data.LoginRequest
import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.error.AuthenticationFailure
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.service.LoginService
import cz.pedro.auth.util.Either
import cz.pedro.auth.util.Either.Left
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/auth"])
class AuthController(
        @Autowired val loginService: LoginService
) {

    @PostMapping(path = ["/login"])
    fun auth(@RequestBody loginRequest: LoginRequest): ResponseEntity<String> {
        return when (val res: Either<AuthenticationFailure, String> = loginService.login(loginRequest.username, loginRequest.password)) {
            is Left -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res.toString())
            else -> ResponseEntity.ok().body(res.toString())
        }
    }

    @PostMapping(path = ["/new"])
    fun register(@RequestBody serviceRequest: ServiceRequest): ResponseEntity<String> {
        return when (val res: Either<RegistrationFailure, String> = loginService.register(serviceRequest)) {
            is Left -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res.toString())
            else -> ResponseEntity.ok().body(res.toString())
        }
    }

    @RequestMapping(path = ["/update"], method = [RequestMethod.PATCH])
    fun update(@RequestBody patch: ServiceRequest): ResponseEntity<String> {
        return when (val res: Either<RegistrationFailure, String> = loginService.update(patch)) {
            is Left -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res.toString())
            else -> ResponseEntity.ok().body(res.toString())
        }
    }
}
