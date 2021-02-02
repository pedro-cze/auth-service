package cz.pedro.auth.util

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.SessionObject
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.error.SessionObjectFailure
import cz.pedro.auth.service.ValidationService
import org.bouncycastle.jcajce.provider.digest.SHA256
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*

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
            val expires = DateTime.now().plusMinutes(10).toDate()
            val sessionObject = SessionObject(
                    UUID.randomUUID(), serviceRequest.username!!, expires, serviceRequest.appId!!
            )
            Either.right(sessionObject)
        } catch (e: Exception) {
            Either.left(SessionObjectFailure.SessionHashGenerationFailure("Hash generation failed: ${e.cause}"))
        }
    }

    private fun generateSessionId(serviceRequest: ServiceRequest): String {
        TODO()
    }
}
