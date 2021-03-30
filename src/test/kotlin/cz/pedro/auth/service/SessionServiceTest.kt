package cz.pedro.auth.service

import cz.pedro.auth.entity.SessionObject
import cz.pedro.auth.repository.SessionObjectRepository
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
class SessionServiceTest {

    @Autowired
    private lateinit var sut: SessionService

    @MockBean
    private lateinit var repository: SessionObjectRepository

    @Test
    fun sessionNotFoundByGivenHashTest() {
        `when`(repository.findByHash(anyString())).thenReturn(eq(null))
        val result = sut.validateSession("test")
        check(result.isLeft())
    }

    @Test
    fun sessionFoundByGivenHashTest() {
        `when`(repository.findByHash(anyString())).thenReturn(mockSessionObject())
        val result = sut.validateSession("testHash")
        check(!result.isLeft())
        check(result.getOrElse { "" } == "testHash")
    }

    private fun mockSessionObject(): SessionObject {
        return SessionObject(
            UUID.randomUUID(),
            "username",
            DateTime.now().plus(10).toDate(),
            "appId",
            "testHash"
        )
    }
}
