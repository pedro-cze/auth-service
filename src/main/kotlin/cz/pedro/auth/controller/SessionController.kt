package cz.pedro.auth.controller

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.data.SessionRequest
import cz.pedro.auth.entity.SessionObject
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.service.AuthService
import cz.pedro.auth.service.SessionService
import cz.pedro.auth.util.Either
import cz.pedro.auth.util.Either.Left
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping(path = ["/session"])
class SessionController(
        @Autowired val authService: AuthService,
        @Autowired val sessionService: SessionService
) {

    @PostMapping(path = ["/login"], consumes = [APPLICATION_JSON_VALUE])
    @CrossOrigin(origins = ["http://localhost:8085", "http://localhost:3000"])
    fun login(@RequestBody sessionRequest: ServiceRequest.SessionRequest): ResponseEntity<String> {
        return when (val res: Either<GeneralFailure, SessionObject> = authService.getSession(sessionRequest)) {
            is Left -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            else -> ResponseEntity.ok().body(res.map { it.hash }.toString())
        }
    }

    @PostMapping(path = ["/valid"])
    @CrossOrigin(origins = ["http://localhost:8085", "http://localhost:3000"])
    fun validate(@RequestBody validationRequest: SessionRequest): ResponseEntity<Unit> {
        return when (sessionService.validateSession(validationRequest.sessionId)) {
            is Left -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Unit>()
            else -> ResponseEntity.ok().build()
        }
    }

    @DeleteMapping(path = ["/invalidate/{sessionId}"])
    @CrossOrigin(origins = ["http://localhost:8085", "http://localhost:3000"])
    fun invalidate(@PathVariable sessionId: String): ResponseEntity<Unit> {
        return when (sessionService.invalidateSession(sessionId)) {
            is Left -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            else -> ResponseEntity.noContent().build()
        }
    }
}
