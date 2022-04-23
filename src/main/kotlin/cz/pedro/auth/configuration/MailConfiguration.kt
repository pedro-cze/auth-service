package cz.pedro.auth.configuration

import cz.pedro.auth.service.RegistrationEmailService
import cz.pedro.auth.service.impl.DummyRegistrationEmailServiceImpl
import cz.pedro.auth.service.impl.RegistrationEmailServiceImpl
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfiguration {

    @Bean
    fun javaMailSender(): JavaMailSender {
        val javaMailSender = JavaMailSenderImpl()

        javaMailSender.host = "localhost"
        javaMailSender.port = 2525

        javaMailSender.javaMailProperties["mail.transport.protocol"] = "smtp"
        javaMailSender.javaMailProperties["mail.smtp.auth"] = false
        javaMailSender.javaMailProperties["mail.smtp.starttls.enable"] = false
        javaMailSender.javaMailProperties["mail.debug"] = true

        return javaMailSender
    }

    @Bean("emailService")
    @ConditionalOnProperty(prefix = "email", name = ["service"], havingValue = "normal")
    fun registrationEmailService(): RegistrationEmailService {
        return RegistrationEmailServiceImpl(javaMailSender())
    }

    @Bean("emailService")
    @ConditionalOnProperty(prefix = "email", name = ["service"], havingValue = "dummy")
    fun dummyRegistrationEmailService(): RegistrationEmailService {
        return DummyRegistrationEmailServiceImpl()
    }
}
