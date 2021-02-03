package cz.pedro.auth.controller

import cz.pedro.auth.data.InvalidateRequest
import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.data.SessionResponse
import cz.pedro.auth.entity.SessionObject
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.service.AuthService
import cz.pedro.auth.util.Either
import cz.pedro.auth.util.Either.Left
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping

@RestController
@RequestMapping(path = ["/session"])
class SessionController(
        @Autowired val authService: AuthService
) {

    @PostMapping(path = ["/login"], consumes = [APPLICATION_JSON_VALUE])
    @CrossOrigin(origins = ["http://localhost:8085"])
    fun login(@RequestBody sessionRequest: ServiceRequest.SessionRequest): ResponseEntity<String> {
        return when (val res: Either<GeneralFailure, SessionObject> = authService.getSession(sessionRequest)) {
            is Left -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            else -> ResponseEntity.ok().body(res.map { SessionResponse(it.sessionId) }.toString())
        }
    }

    @PostMapping(path = ["/valid"])
    @CrossOrigin(origins = ["http://localhost:8085"])
    fun validate(@RequestBody sessionId: String): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/invalidate")
    fun invalidate(@RequestBody invalidateRequest: InvalidateRequest): ResponseEntity<Unit> {
        return when (val res: Either<GeneralFailure, String> = authService.invalidateSession(invalidateRequest.sessionId)) {
            is Left -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            else -> ResponseEntity.noContent().build()
        }
    }
}
