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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/session"])
class SessionController(
        @Autowired val authService: AuthService
) {

    @PostMapping(path = ["/login"])
    fun login(@RequestBody sessionRequest: ServiceRequest.SessionRequest): ResponseEntity<String> {
        return when (val res: Either<GeneralFailure, SessionObject> = authService.getSession(sessionRequest)) {
            is Left -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            else -> ResponseEntity.ok().body(res.map { SessionResponse(it.sessionId) }.toString())
        }
    }

    @DeleteMapping("/invalidate")
    fun invalidate(@RequestBody invalidateRequest: InvalidateRequest): ResponseEntity<Unit> {
        return when (val res: Either<GeneralFailure, String> = authService.invalidateSession(invalidateRequest.sessionId)) {
            is Left -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            else -> ResponseEntity.noContent().build()
        }
    }
}
