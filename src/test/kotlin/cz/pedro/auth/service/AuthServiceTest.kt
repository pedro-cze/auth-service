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
            .thenReturn(User(userId, "INVOICE_APP", "John Doe", "hashed", "USER", true))
        Mockito.`when`(encoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true)
        Mockito.`when`(generationService.generateToken(AuthRequester(User(userId, "INVOICE_APP", "John Doe", "hashed", "USER", true)))).thenReturn("token")
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
        Mockito.`when`(userRepository.findByUsernameAndServiceName(Mockito.anyString(), Mockito.anyString())).thenReturn(User(UUID.randomUUID(), "INVOICE_APP", "John Doe", "foo", "USER", true))
        val result = authService.login(mockAuthRequest)
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }

    @Test
    fun emptyPasswordTest() {
        val mockAuthRequest = ServiceRequest.AuthenticationRequest("INVOICE_APP", "John Doe", "")
        Mockito.`when`(userRepository.findByUsernameAndServiceName(Mockito.anyString(), Mockito.anyString())).thenReturn(User(UUID.randomUUID(), "INVOICE_APP", "John Doe", "foo", "USER", true))
        val result = authService.login(mockAuthRequest)
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }

    @Test
    fun blankPasswordTest() {
        val mockAuthRequest = ServiceRequest.AuthenticationRequest("INVOICE_APP", "John Doe", "   ")
        Mockito.`when`(userRepository.findByUsernameAndServiceName(Mockito.anyString(), Mockito.anyString())).thenReturn(User(UUID.randomUUID(), "INVOICE_APP", "John Doe", "foo", "USER", true))
        val result = authService.login(mockAuthRequest)
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }

    @Test
    fun notActiveUserTest() {
        val mockAuthRequest = ServiceRequest.AuthenticationRequest("INVOICE_APP", "John Doe", "Test1234")
        Mockito.`when`(userRepository.findByUsernameAndServiceName(Mockito.anyString(), Mockito.anyString())).thenReturn(User(UUID.randomUUID(), "INVOICE_APP", "John Doe", "foo", "USER", false))
        Mockito.`when`(encoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true)
        val result = authService.login(mockAuthRequest)
        check(result.isLeft())
        check(result.fold({ customError -> customError is AuthenticationFailure.Unauthorized }, { false }))
    }

    @Test
    fun patchUserTest_success() {
        val userId = UUID.randomUUID()
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false)))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        val request = ServiceRequest.PatchRequest(null, null, null, active = true)
        val result = authService.update(UUID.randomUUID(), request)
        check(!result.isLeft())
        check(result.toString() == "John Doe")
    }

    @Test
    fun patchUserTest_emptyUsername() {
        val request = ServiceRequest.PatchRequest("", null, null, active = true)
        val result = authService.update(UUID.randomUUID(), request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyUsername }, { false }))
    }

    @Test
    fun patchUserTest_blankUsername() {
        val request = ServiceRequest.PatchRequest("   ", null, null, active = true)
        val result = authService.update(UUID.randomUUID(), request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyUsername }, { false }))
    }

    @Test
    fun patchUserTest_usernameTaken() {
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(UUID.randomUUID(), "INVOICE_APP", "John Doe", "foo", "USER", false))
        val request = ServiceRequest.PatchRequest("John Doe", null, null, active = true)
        val result = authService.update(UUID.randomUUID(), request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is RegistrationFailure.UsernameAlreadyUsed }, { false }))
    }

    @Test
    fun patchUserTest_emptyPassword() {
        val request = ServiceRequest.PatchRequest(null, "", null, active = true)
        val result = authService.update(UUID.randomUUID(), request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyPassword }, { false }))
    }

    @Test
    fun patchUserTest_emptyAuthorities() {
        val userId = UUID.randomUUID()
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false)))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        val request = ServiceRequest.PatchRequest(null, null, "", true)
        val result = authService.update(UUID.randomUUID(), request)
        check(!result.isLeft())
        check(result.toString() == "John Doe")
    }

    @Test
    fun patchUserTest_removeAuthority() {
        val userId = UUID.randomUUID()
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false)))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        val request = ServiceRequest.PatchRequest(null, null, "", true)
        val result = authService.update(UUID.randomUUID(), request)
        check(!result.isLeft())
        check(result.toString() == "John Doe")
    }

    @Test
    fun patchUserTest_addAuthority() {
        val userId = UUID.randomUUID()
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false)))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        val request = ServiceRequest.PatchRequest(null, null, "USER, ADMIN", true)
        val result = authService.update(UUID.randomUUID(), request)
        check(!result.isLeft())
        check(result.toString() == "John Doe")
    }

    @Test
    fun patchUserTest_invalidAuthority() {
        val userId = UUID.randomUUID()
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false)))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        val request = ServiceRequest.PatchRequest(null, null, "USER, ADMIN, TEST", true)
        val result = authService.update(UUID.randomUUID(), request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.InvalidRequest }, { false }))
    }

    @Test
    fun patchUserTest_deactivate() {
        val userId = UUID.randomUUID()
        Mockito.`when`(userRepository.findByUsername(Mockito.anyString())).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", true))
        Mockito.`when`(userRepository.findById(Mockito.any(UUID::class.java))).thenReturn(Optional.of(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", true)))
        Mockito.`when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(User(userId, "INVOICE_APP", "John Doe", "foo", "USER", false))
        val request = ServiceRequest.PatchRequest(null, null, null, false)
        val result = authService.update(UUID.randomUUID(), request)
        check(!result.isLeft())
        check(result.toString() == "John Doe")
    }
}
