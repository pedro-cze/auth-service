package cz.pedro.auth.controller

import cz.pedro.auth.data.RegistrationRequest
import cz.pedro.auth.entity.RegistrationStatus
import cz.pedro.auth.service.RegistrationService
import cz.pedro.auth.util.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(path = ["/admin"])
class RegistrationController(
        @Autowired val registrationService: RegistrationService,
        @Autowired val encoder: BCryptPasswordEncoder
) {

    @PostMapping(path = ["/registration"])
    fun registration(@RequestBody registrationRequest: RegistrationRequest): ResponseEntity<String> {
        val encoded = encoder.encode(registrationRequest.password)
        return when (val result = registrationService.register(registrationRequest.username, encoded)) {
            is Either.Left -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.toString())
            else -> ResponseEntity.status(HttpStatus.CREATED).body(result.toString())
        }
    }

    @PostMapping(path = ["/registration/{registrationId}"])
    fun confirm(@PathVariable registrationId: UUID): ResponseEntity<String> {
        return when (val result = registrationService.update(registrationId, RegistrationStatus.CONFIRMED)) {
            is Either.Left -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.toString())
            else -> ResponseEntity.status(HttpStatus.NO_CONTENT).body(result.toString())
        }
    }

    @DeleteMapping(path = ["/registration/{registrationId}"])
    fun reject(@PathVariable registrationId: UUID): ResponseEntity<String> {
        return when (val result = registrationService.update(registrationId, RegistrationStatus.REJECTED)) {
            is Either.Left -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.toString())
            else -> ResponseEntity.status(HttpStatus.NO_CONTENT).body(result.toString())
        }
    }
}
