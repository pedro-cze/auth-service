package cz.pedro.auth.service

import cz.pedro.auth.entity.User
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.util.CustomError
import cz.pedro.auth.util.Either
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var userRepository: UserRepository

    @Test
    fun successfulLoginTest() {
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(1L, "John Doe", "hashed"))
        val result = authService.login("John Doe", "hashed")
        check(result.toString() == "token")
    }

    @Test
    fun emptyUserNameLoginTest() {
        val result = authService.login("", "")
        check(result.isLeft())
        check(result.fold(isEmptyUsername, { false }))
    }

    @Test
    fun userNotFoundTest() {
        val result: Either<CustomError, String> = authService.login("John Doe", "password")
        check(result.isLeft())
        check(result.fold(isUserNotFound, { false }))
    }

    @Test
    fun passwordNotMatchesTest() {
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(1L, "John Doe", "foo"))
        val result = authService.login("John Doe", "bar")
        check(result.isLeft())
        check(result.fold(isWrongPassword, { false }))
    }

    @Test
    fun emptyPasswordTest() {
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(1L, "John Doe", "foo"))
        val result = authService.login("John Doe", "")
        check(result.isLeft())
        check(result.fold(isWrongPassword, { false }))
    }

    private val isUserNotFound: (CustomError) -> Boolean = { it is CustomError.UserNotFound }
    private val isEmptyUsername: (CustomError) -> Boolean = { it is CustomError.EmptyUsername }
    private val isWrongPassword: (CustomError) -> Boolean = { it is CustomError.Unauthorized }
}
