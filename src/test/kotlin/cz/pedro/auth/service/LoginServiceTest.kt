package cz.pedro.auth.service

import cz.pedro.auth.entity.User
import cz.pedro.auth.error.AuthenticationFailure
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.security.model.AuthRequester
import cz.pedro.auth.util.Either
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class LoginServiceTest {

    @Autowired
    private lateinit var loginService: LoginService

    @MockBean
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var generationService: TokenGenerationService

    @MockBean
    private lateinit var encoder: BCryptPasswordEncoder

    @Test
    fun successfulLoginTest() {
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(1L, "John Doe", "hashed", "USER", true))
        Mockito.`when`(encoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true)
        Mockito.`when`(generationService.generateToken(AuthRequester(User(1L, "John Doe", "hashed", "USER", true)))).thenReturn("token")
        val result = loginService.login("John Doe", "hashed")
        check(!result.isLeft())
        check(result.fold({ false }, { res -> res == "token" }))
    }

    @Test
    fun emptyUserNameLoginTest() {
        val result = loginService.login("", "")
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.EmptyUsername }, { false }))
    }

    @Test
    fun userNotFoundTest() {
        val result: Either<AuthenticationFailure, String> = loginService.login("John Doe", "password")
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.UserNotFound }, { false }))
    }

    @Test
    fun passwordNotMatchesTest() {
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(1L, "John Doe", "foo", "USER", true))
        val result = loginService.login("John Doe", "bar")
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }

    @Test
    fun emptyPasswordTest() {
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(1L, "John Doe", "foo", "USER", true))
        val result = loginService.login("John Doe", "")
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }

    @Test
    fun notActiveUser() {
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(1L, "John Doe", "foo", "USER", false))
        Mockito.`when`(encoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true)
        val result = loginService.login("John Doe", "Test1234")
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }
}
