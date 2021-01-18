package cz.pedro.auth.util

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.SessionObject
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.error.SessionObjectFailure
import cz.pedro.auth.service.ValidationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class SessionObjectGeneratorImpl(
        @Autowired
        val validationService: ValidationService
) : SessionObjectGenerator {

    override fun generateSessionObject(serviceRequest: ServiceRequest): Either<GeneralFailure, SessionObject> {
        return validationService.validate(serviceRequest)
                .flatMap { buildSessionObject(it) }
    }

    private fun buildSessionObject(serviceRequest: ServiceRequest): Either<GeneralFailure, SessionObject> {
        return try {
            val hash = ""
            val sessionObject = SessionObject(
                    UUID.randomUUID(), serviceRequest.username!!, LocalDateTime.now(), serviceRequest.appId!!
            )
            Either.right(sessionObject)
        } catch (e: Exception) {
            Either.left(SessionObjectFailure.SessionHashGenerationFailure("Hash generation failed: ${e.cause}"))
        }
    }
}
