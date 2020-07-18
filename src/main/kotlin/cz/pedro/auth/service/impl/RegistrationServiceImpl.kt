package cz.pedro.auth.service.impl

import cz.pedro.auth.entity.Registration
import cz.pedro.auth.entity.RegistrationStatus
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.repository.RegistrationRepository
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.service.RegistrationService
import cz.pedro.auth.util.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class RegistrationServiceImpl(
        @Autowired val userRepository: UserRepository,
        @Autowired val registrationRepository: RegistrationRepository
) : RegistrationService {

    @Transactional
    override fun register(username: String, password: String): Either<RegistrationFailure, Long> {
        return checkUsers(username)
                .flatMap { checkRegistrations(it) }
                .flatMap { saveRegistration(it, password) }
    }

    private fun checkUsers(username: String): Either<RegistrationFailure, String> {
        return if (userRepository.findByUsername(username) == null) {
            Either.right(username)
        } else {
            Either.left(RegistrationFailure.UsernameAlreadyUsed("Username already taken."))
        }
    }

    private fun checkRegistrations(username: String): Either<RegistrationFailure, String> {
        val registration = registrationRepository.findByUsername(username)
        return if (registration?.status == RegistrationStatus.PENDING) {
            Either.left(RegistrationFailure.PendingRegistration("Pending registration."))
        } else {
            Either.right(username)
        }
    }

    private fun saveRegistration(username: String, password: String): Either<RegistrationFailure, Long> {
        val registration = Registration(
                username = username,
                password = password,
                status = RegistrationStatus.PENDING
        )
        val result = registrationRepository.save(registration)
        return if (result.id == null) {
            Either.left(RegistrationFailure.SavingFailed("Something went wrong while trying to save registration."))
        } else {
            Either.right(result.id!!)
        }
    }
}
