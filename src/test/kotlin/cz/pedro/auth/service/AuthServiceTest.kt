package cz.pedro.auth.service

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.entity.User
import cz.pedro.auth.error.AuthenticationFailure
import cz.pedro.auth.error.GeneralFailure
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.error.ValidationFailure
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
import java.util.UUID
import java.util.Optional

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private lateinit var authService: AuthService
    @Autowired
    private lateinit var registrationService: RegistrationService
    @MockBean
    private lateinit var userRepository: UserRepository
    @MockBean
    private lateinit var generationService: TokenGenerationService
    @MockBean
    private lateinit var encoder: BCryptPasswordEncoder

    @Test
    fun successfulLoginTest() {
        val userId = UUID.randomUUID()
        val mockAuthRequest = ServiceRequest.AuthenticationRequest("INVOICE_APP", "John Doe", "hashed")
        Mockito.`when`(userRepository.findByUsernameAndServiceName(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(User(
                userId,
                serviceName = "INVOICE_APP",
                firstName = "John",
                lastName = "Doe",
                username = "username",
                password = "password",
                email = "email",
                authorities = "USER",
                active = true)
            )
        Mockito.`when`(encoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true)
        Mockito.`when`(generationService.generateToken(AuthRequester(User(
            userId,
            serviceName = "INVOICE_APP",
            firstName = "John",
            lastName = "Doe",
            username = "username",
            password = "password",
            email = "email",
            authorities = "USER",
            active = true)))).thenReturn("token")
        val result = authService.login(mockAuthRequest)
        check(!result.isLeft())
        check(result.fold({ false }, { res -> res == "token" }))
    }

    @Test
    fun emptyUserNameLoginTest() {
        val result = authService.login(ServiceRequest.AuthenticationRequest("INVOICE_APP", "", ""))
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyUsername }, { false }))
    }

    @Test
    fun blankUserNameLoginTest() {
        val result = authService.login(ServiceRequest.AuthenticationRequest("INVOICE_APP", "   ", ""))
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyUsername }, { false }))
    }

    @Test
    fun userNotFoundTest() {
        val mockAuthRequest = ServiceRequest.AuthenticationRequest("INVOICE_APP", "John Doe", "password")
        val result: Either<GeneralFailure, String> = authService.login(mockAuthRequest)
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.UserNotFound }, { false }))
    }

    @Test
    fun passwordNotMatchesTest() {
        val mockAuthRequest = ServiceRequest.AuthenticationRequest("INVOICE_APP", "John Doe", "bar")
        Mockito.`when`(userRepository.findByUsernameAndServiceName(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(mockUser(UUID.randomUUID(), true))
        val result = authService.login(mockAuthRequest)
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }

    @Test
    fun emptyPasswordTest() {
        val mockAuthRequest = ServiceRequest.AuthenticationRequest("INVOICE_APP", "John Doe", "")
        Mockito.`when`(userRepository.findByUsernameAndServiceName(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(mockUser(UUID.randomUUID(), true))
        val result = authService.login(mockAuthRequest)
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }

    @Test
    fun blankPasswordTest() {
        val mockAuthRequest = ServiceRequest.AuthenticationRequest("INVOICE_APP", "John Doe", "   ")
        Mockito.`when`(userRepository.findByUsernameAndServiceName(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(mockUser(UUID.randomUUID(), true))
        val result = authService.login(mockAuthRequest)
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }

    @Test
    fun notActiveUserTest() {
        val mockAuthRequest = ServiceRequest.AuthenticationRequest("INVOICE_APP", "John Doe", "Test1234")
        Mockito.`when`(userRepository.findByUsernameAndServiceName(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(mockUser(UUID.randomUUID(), false))
        Mockito.`when`(encoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true)
        val result = authService.login(mockAuthRequest)
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }

    @Test
    fun patchUserTest_success() {
        val userId = UUID.randomUUID()
        val mockUser = User(
            userId,
            serviceName = "INVOICE_APP",
            firstName = "John",
            lastName = "Doe",
            username = "username",
            password = "password",
            email = "email",
            authorities = "USER",
            active = true
        )
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(mockUser)
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(mockUser))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(mockUser)
        val request = ServiceRequest.PatchRequest(null, null, null, null, null, active = true)
        val result = authService.update(UUID.randomUUID(), request)
        check(!result.isLeft())
        check(result.toString() == "username")
    }

    @Test
    fun patchUserTest_emptyUsername() {
        val request = ServiceRequest.PatchRequest("", null, null, null, null, active = true)
        val result = authService.update(UUID.randomUUID(), request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyUsername }, { false }))
    }

    @Test
    fun patchUserTest_blankUsername() {
        val request = ServiceRequest.PatchRequest("   ", null, null, null, null, true)
        val result = authService.update(UUID.randomUUID(), request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyUsername }, { false }))
    }

    @Test
    fun patchUserTest_usernameTaken() {
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(mockUser(UUID.randomUUID(), false))
        val request = ServiceRequest.PatchRequest("John Doe", null, null, null, null, active = true)
        val result = authService.update(UUID.randomUUID(), request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is RegistrationFailure.UsernameAlreadyUsed }, { false }))
    }

    @Test
    fun patchUserTest_emptyPassword() {
        val request = ServiceRequest.PatchRequest(null, "", null, null, null, true)
        val result = authService.update(UUID.randomUUID(), request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyPassword }, { false }))
    }

    @Test
    fun patchUserTest_emptyAuthorities() {
        val mockUser = mockUser(UUID.randomUUID(), false)
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(mockUser)
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(mockUser))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(mockUser)
        val request = ServiceRequest.PatchRequest(null, null, "", null, null, true)
        val result = authService.update(UUID.randomUUID(), request)
        check(!result.isLeft())
        check(result.toString() == "username")
    }

    @Test
    fun patchUserTest_removeAuthority() {
        val mockUser = mockUser(UUID.randomUUID(), false)
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(mockUser)
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(mockUser))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(mockUser)
        val request = ServiceRequest.PatchRequest(null, null, "", null, null, true)
        val result = authService.update(UUID.randomUUID(), request)
        check(!result.isLeft())
        check(result.toString() == "username")
    }

    @Test
    fun patchUserTest_addAuthority() {
        val mockUser = mockUser(UUID.randomUUID(), false)
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(mockUser)
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(mockUser))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(mockUser)
        val request = ServiceRequest.PatchRequest(null, null, "USER, ADMIN", null, null, true)
        val result = authService.update(UUID.randomUUID(), request)
        check(!result.isLeft())
        check(result.toString() == "username")
    }

    @Test
    fun patchUserTest_invalidAuthority() {
        val userId = UUID.randomUUID()
        val mockUser = mockUser(userId, false)
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(mockUser)
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(mockUser))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(mockUser)
        val request = ServiceRequest.PatchRequest(null, null, "USER, ADMIN, TEST", null, null,  true)
        val result = authService.update(UUID.randomUUID(), request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.InvalidRequest }, { false }))
    }

    @Test
    fun patchUserTest_deactivate() {
        val mockUser = mockUser(UUID.randomUUID(), true)
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(mockUser)
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(mockUser))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(mockUser)
        val request = ServiceRequest.PatchRequest(null, null, null, null, null, false)
        val result = authService.update(UUID.randomUUID(), request)
        check(!result.isLeft())
        check(result.toString() == "username")
    }

    private fun mockUser(userId: UUID, active: Boolean): User {
        return User(
            userId,
            serviceName = "INVOICE_APP",
            firstName = "John",
            lastName = "Doe",
            username = "username",
            password = "password",
            email = "email",
            authorities = "USER",
            active = active
        )
    }
}
