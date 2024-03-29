package cz.pedro.auth.controller

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.service.AuthService
import cz.pedro.auth.util.Either
import cz.pedro.auth.util.Either.Left
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID

@RestController
@RequestMapping(path = ["/auth"])
class AuthController(
        @Autowired val authService: AuthService
) {

    @PostMapping(path = ["/login"])
    @CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080"])
    fun auth(@RequestBody request: ServiceRequest.AuthenticationRequest): ResponseEntity<String> {
        return when (val res: Either<GeneralFailure, String> = authService.login(request)) {
            is Left -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res.toString())
            else -> ResponseEntity.ok().body(res.toString())
        }
    }

    @RequestMapping(path = ["/update/{userId}"], method = [RequestMethod.PATCH])
    fun update(@PathVariable userId: UUID, @RequestBody patch: ServiceRequest.PatchRequest): ResponseEntity<String> {
        return when (val res: Either<GeneralFailure, String> = authService.update(userId, patch)) {
            is Left -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res.toString())
            else -> ResponseEntity.ok().body(res.toString())
        }
    }
}
