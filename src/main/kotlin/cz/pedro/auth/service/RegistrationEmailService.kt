package cz.pedro.auth.service

interface RegistrationEmailService {

    fun sendConfirmationEmail(recipient: String, registrationHash: String)
}
