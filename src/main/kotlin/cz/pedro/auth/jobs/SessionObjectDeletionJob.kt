package cz.pedro.auth.jobs

import cz.pedro.auth.entity.SessionObject
import cz.pedro.auth.repository.SessionObjectRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

private val logger = KotlinLogging.logger {}

@Service
@EnableScheduling
class SessionObjectDeletionJob(
        @Autowired val sessionObjectRepository: SessionObjectRepository
) {

    @Transactional
    @Scheduled(cron = "5 * * * * *")
    fun findAndDeleteExpiredSessions() {
        logger.debug { "Starting cron job for expired session objects deletion." }
        val expiredSessions = findExpiredSessionObjects()
        deleteBatchOfExpiredSessionObjects(expiredSessions)
        logger.debug { "Deleted ${expiredSessions.size} expired objects. Next check in 5 minutes." }
    }

    private fun findExpiredSessionObjects(): List<SessionObject> {
        return sessionObjectRepository.findAllWithCreationDateTimeBefore(Date())
    }

    private fun deleteBatchOfExpiredSessionObjects(entities: List<SessionObject>) {
        sessionObjectRepository.deleteInBatch(entities)
    }
}
