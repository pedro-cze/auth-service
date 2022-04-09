package cz.pedro.auth.util

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.SessionObject
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.error.SessionObjectFailure
import cz.pedro.auth.service.ValidationService
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.UUID

@Component
class SessionObjectGeneratorImpl(
        @Autowired
        val validationService: ValidationService
) : SessionObjectGenerator {

    override fun generateSessionObject(sessionRequest: ServiceRequest): Either<GeneralFailure, SessionObject> {
        return validationService.validate(sessionRequest)
                .flatMap { buildSessionObject(it) }
    }

    private fun buildSessionObject(serviceRequest: ServiceRequest): Either<GeneralFailure, SessionObject> {
        return try {
            val expires = DateTime.now().plusMinutes(10).toDate()
            val sessionObject = SessionObject(
                UUID.randomUUID(), serviceRequest.username!!, expires, serviceRequest.appId!!, generateSessionId(serviceRequest).toString()
            )
            Either.right(sessionObject)
        } catch (e: Exception) {
            Either.left(SessionObjectFailure.SessionObjectGenerationFailure("Session object generation failed: ${e.cause}"))
        }
    }

    private fun generateSessionId(serviceRequest: ServiceRequest): Either<GeneralFailure, String> {
        return try {
            val sessionId = MessageDigest.getInstance("SHA-256").digest(serviceRequest.toString().toByteArray()).toHex()
            Either.right(sessionId)
        } catch (e: Exception) {
            Either.left(SessionObjectFailure.SessionHashGenerationFailure("Hash generation failed: ${e.cause}"))
        }
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
