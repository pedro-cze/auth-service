package cz.pedro.auth.service.impl

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.Registration
import cz.pedro.auth.entity.RegistrationStatus
import cz.pedro.auth.entity.User
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.repository.RegistrationRepository
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.service.RegistrationEmailService
import cz.pedro.auth.service.RegistrationService
import cz.pedro.auth.service.ValidationService
import cz.pedro.auth.util.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class RegistrationServiceImpl(
        @Autowired val validationService: ValidationService,
        @Autowired val registrationRepository: RegistrationRepository,
        @Autowired val userRepository: UserRepository,
        @Autowired val encoder: BCryptPasswordEncoder,
        @Autowired val emailService: RegistrationEmailService
) : RegistrationService {

    @Transactional
    override fun register(request: ServiceRequest.RegistrationRequest): Either<GeneralFailure, String> {
        return validationService.validate(request)
                .flatMap { createRegistration(request) }
                .flatMap { sendConfirmationEmail(it) }
                .flatMap { createUser(request) }
                .map { it.username }
    }

    private fun createRegistration(request: ServiceRequest.RegistrationRequest): Either<GeneralFailure, Registration> {
        val registration = Registration(
                username = request.username,
                password = encoder.encode(request.password),
                hash = BigInteger(1, SecureRandom.getInstance("SHA1PRNG").generateSeed(32)).toString(16))
        return try {
            registrationRepository.save(registration)
            Either.right(registration)
        } catch (e: Exception) {
            Either.left(RegistrationFailure.SavingFailed())
        }
    }

    @Transactional
    override fun confirmRegistration(hash: String): Either<GeneralFailure, String> {
        return findRegistration(hash)
                .flatMap { validateRegistration(it) }
                .flatMap { confirmRegistrationStatus(it) }
                .flatMap { activateUser(it) }
    }

    private fun findRegistration(hash: String): Either<GeneralFailure, Registration> {
        val registration = registrationRepository.findByHash(hash)
        registration?.let {
            return Either.right(it)
        }
        return Either.left(RegistrationFailure.RegistrationNotFound())
    }

    private fun validateRegistration(registration: Registration): Either<GeneralFailure, Registration> {
        return registrationNotExpired(registration)
                .flatMap { registrationPending(it) }
    }

    private fun registrationNotExpired(registration: Registration): Either<GeneralFailure, Registration> {
        return if (registration.created.toInstant().plus(24, ChronoUnit.HOURS).isBefore(Instant.now())) {
            Either.left(RegistrationFailure.RegistrationExpired())
        } else {
            Either.right(registration)
        }
    }

    private fun registrationPending(registration: Registration): Either<GeneralFailure, Registration> {
        return if (RegistrationStatus.PENDING != registration.status) {
            Either.left(RegistrationFailure.RegistrationNotPending())
        } else {
            Either.right(registration)
        }
    }

    private fun confirmRegistrationStatus(registration: Registration): Either<GeneralFailure, Registration> {
        return try {
            registration.status = RegistrationStatus.CONFIRMED
            registrationRepository.save(registration)
            Either.right(registration)
        } catch (e: Exception) {
            Either.left(RegistrationFailure.SavingFailed())
        }
    }

    private fun createUser(request: ServiceRequest.RegistrationRequest): Either<GeneralFailure, User> {
        val user = User(
            null,
            request.appId,
            firstName = "firstname",
            lastName = "lastname",
            email = "email",
            username = request.username,
            password = encoder.encode(request.password),
            active = false,
            authorities = request.authorities
        )
        val res: User = userRepository.save(user)
        return if (res.id == null) {
            Either.left(RegistrationFailure.SavingFailed())
        } else {
            Either.right(res)
        }
    }

    private fun activateUser(registration: Registration): Either<GeneralFailure, String> {
        val user = userRepository.findByUsername(registration.username)
        user?.let {
            if (user.active) {
                return Either.left(RegistrationFailure.ActiveUserDetected())
            }
            user.active = true
            userRepository.save(user)
            return Either.right(it.username)
        }
        return Either.left(RegistrationFailure.UserNotFound())
    }

    private fun sendConfirmationEmail(registration: Registration): Either<GeneralFailure, String> {
        return try {
            emailService.sendConfirmationEmail(registration.username, registration.hash)
            Either.right(registration.username)
        } catch (e: Exception) {
            Either.left(RegistrationFailure.EmailNotSent())
        }
    }
}
