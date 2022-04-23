package cz.pedro.auth.controller

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.service.RegistrationService
import cz.pedro.auth.util.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping(path = ["/signup"])
class RegistrationController(
        @Autowired val registrationService: RegistrationService
) {

    @PostMapping(path = ["/new"])
    fun register(@RequestBody serviceRequest: ServiceRequest.RegistrationRequest): ResponseEntity<String> {
        return when (val res: Either<GeneralFailure, String> = registrationService.register(serviceRequest)) {
            is Either.Left -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res.toString())
            else -> ResponseEntity.status(HttpStatus.CREATED).body(res.toString())
        }
    }

    @GetMapping(path = ["/reg-confirm"])
    fun confirmRegistration(@RequestParam hash: String): ResponseEntity<String> {
        return when (val res: Either<GeneralFailure, String> = registrationService.confirmRegistration(hash)) {
            is Either.Left -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res.toString())
            else -> ResponseEntity.status(HttpStatus.OK).body(res.toString())
        }
    }
}
