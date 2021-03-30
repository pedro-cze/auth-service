package cz.pedro.auth.repository

import cz.pedro.auth.entity.SessionObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Date
import java.util.UUID

@Repository
interface SessionObjectRepository : JpaRepository<SessionObject, UUID> {

    @Query("select so from SessionObject so where so.expires <= :currentDateTime")
    fun findAllWithCreationDateTimeBefore(@Param("currentDateTime") currentDateTime: Date): List<SessionObject>

    fun findByHash(hash: String): SessionObject?
}
