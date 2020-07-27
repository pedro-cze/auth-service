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
import java.util.UUID
import javax.transaction.Transactional

@Service
class RegistrationServiceImpl(
        @Autowired val userRepository: UserRepository,
        @Autowired val registrationRepository: RegistrationRepository
) : RegistrationService {

    @Transactional
    override fun register(username: String, password: String): Either<RegistrationFailure, UUID> {
        return checkUsers(username)
                .flatMap { checkRegistrations(it) }
                .flatMap { saveRegistration(it, password) }
    }

    @Transactional
    override fun update(registrationId: UUID, status: RegistrationStatus): Either<RegistrationFailure, UUID> {
        return loadRegistration(registrationId)
                .flatMap { checkStatus(it) }
                .flatMap { updateStatus(it, status) }
    }

    private fun loadRegistration(registrationId: UUID): Either<RegistrationFailure, Registration> {
        val registration = registrationRepository.findById(registrationId)
        return if (registration.isPresent) {
            Either.right(registration.get())
        } else {
            Either.left(RegistrationFailure.RegistrationNotFound())
        }
    }

    private fun checkStatus(registration: Registration): Either<RegistrationFailure, Registration> {
        return if (registration.status != RegistrationStatus.PENDING) {
            Either.left(RegistrationFailure.UnexpectedStatus("Cannot update registration with status: ${registration.status}"))
        } else {
            Either.right(registration)
        }
    }

    private fun updateStatus(registration: Registration, status: RegistrationStatus): Either<RegistrationFailure, UUID> {
        registration.status = status
        val res = registrationRepository.save(registration)
        return if (res.id == null) {
            Either.left(RegistrationFailure.SavingFailed())
        } else {
            Either.right(res.id!!)
        }
    }

    private fun checkUsers(username: String): Either<RegistrationFailure, String> {
        return if (userRepository.findByUsername(username) == null) {
            Either.right(username)
        } else {
            Either.left(RegistrationFailure.UsernameAlreadyUsed())
        }
    }

    private fun checkRegistrations(username: String): Either<RegistrationFailure, String> {
        val registration = registrationRepository.findByUsername(username)
        return if (registration?.status == RegistrationStatus.PENDING) {
            Either.left(RegistrationFailure.PendingRegistration())
        } else {
            Either.right(username)
        }
    }

    private fun saveRegistration(username: String, password: String): Either<RegistrationFailure, UUID> {
        val registration = Registration(
                username = username,
                password = password,
                status = RegistrationStatus.PENDING
        )
        val result = registrationRepository.save(registration)
        return if (result.id == null) {
            Either.left(RegistrationFailure.SavingFailed())
        } else {
            Either.right(result.id!!)
        }
    }
}
