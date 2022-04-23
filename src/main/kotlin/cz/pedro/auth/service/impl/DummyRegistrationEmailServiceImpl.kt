package cz.pedro.auth.service.impl

import cz.pedro.auth.service.RegistrationEmailService
import org.springframework.stereotype.Service

@Service
class DummyRegistrationEmailServiceImpl : RegistrationEmailService {

    override fun sendConfirmationEmail(recipient: String, registrationHash: String) {
        // do nothing
    }
}
