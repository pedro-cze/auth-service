package cz.pedro.auth.service.impl

import cz.pedro.auth.error.SessionObjectFailure
import cz.pedro.auth.repository.SessionObjectRepository
import cz.pedro.auth.service.SessionService
import cz.pedro.auth.util.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SessionServiceImpl(
    @Autowired val sessionObjectRepository: SessionObjectRepository) : SessionService {

    @Transactional
    override fun validateSession(sessionHash: String): Either<SessionObjectFailure.SessionObjectNotFound, String> {
        return if (sessionObjectRepository.findByHash(sessionHash) == null) {
            Either.left(SessionObjectFailure.SessionObjectNotFound(""))
        } else {
            Either.right(sessionHash)
        }
    }

    @Transactional
    override fun invalidateSession(sessionHash: String): Either<SessionObjectFailure, String> {
        sessionObjectRepository.findByHash(sessionHash)?.let {
            sessionObjectRepository.delete(it)
            return Either.right(sessionHash)
        }
        return Either.left(SessionObjectFailure.SessionObjectNotFound())
    }
}
