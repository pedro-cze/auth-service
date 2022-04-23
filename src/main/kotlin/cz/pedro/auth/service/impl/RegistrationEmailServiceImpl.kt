package cz.pedro.auth.service.impl

import cz.pedro.auth.service.RegistrationEmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import javax.mail.Message
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart

@Service
class RegistrationEmailServiceImpl(
        @Autowired val javaMailSender: JavaMailSender
) : RegistrationEmailService {

    override fun sendConfirmationEmail(recipient: String, registrationHash: String) {
        val message = javaMailSender.createMimeMessage()

        val link = "<a href='http://localhost:8083/signup/reg-confirm?hash=$registrationHash'>$recipient</a>"

        val mimeBodyPart = MimeBodyPart()
        mimeBodyPart.setContent(link, "text/html; charset=utf-8")

        val multipart = MimeMultipart()
        multipart.addBodyPart(mimeBodyPart)

        message.subject = "Registration confimration"
        message.addRecipients(Message.RecipientType.TO, recipient)
        message.setContent(multipart)
        javaMailSender.send(message)
    }
}
