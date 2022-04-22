package cz.pedro.auth.service

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.error.RegistrationFailure
import cz.pedro.auth.error.ValidationFailure
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RegistrationServiceTest {

    @Autowired
    private lateinit var registrationService: RegistrationService

    @Test
    fun `create new registration`() {
        val registrationRequest = ServiceRequest.RegistrationRequest("INVOICE_APP", "foo@doe.com", "Test1234", "USER")
        val res = registrationService.register(registrationRequest)
        Assertions.assertThat(res.isLeft()).isFalse()
    }

    @Test
    fun registerUser_success() {
        val registrationRequest = ServiceRequest.RegistrationRequest(appId = "INVOICE_APP", username = "Jack Doe", password = "Test1234",
                authorities = "USER, ADMIN")
        val result = registrationService.register(registrationRequest)
        check(!result.isLeft()) { result }
        check(result.toString() == "Jack Doe")
    }

    @Test
    fun registerUserTest_emptyUsername() {
        val registrationRequest = ServiceRequest.RegistrationRequest(appId = "INVOICE_APP", username = "", password = "test1234", authorities =
        "USER, ADMIN")
        val result = registrationService.register(registrationRequest)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyUsername }, { false }))
    }

    @Test
    fun registerUserTest_blankUsername() {
        val request = ServiceRequest.RegistrationRequest(appId = "INVOICE_APP", username = "   ", password = "test1234", authorities = "USER, ADMIN")
        val result = registrationService.register(request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyUsername }, { false }))
    }

    @Test
    fun registerUserTest_emptyPassword() {
        val request = ServiceRequest.RegistrationRequest(appId = "INVOICE_APP", username = "John Doe", password = "", authorities = "USER, ADMIN")
        val result = registrationService.register(request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyPassword }, { false }))
    }

    @Test
    fun registerUserTest_blankPassword() {
        val request = ServiceRequest.RegistrationRequest(appId = "INVOICE_APP", username = "John Doe", password = "   ", authorities = "USER, ADMIN")
        val result = registrationService.register(request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is ValidationFailure.NullOrEmptyPassword }, { false }))
    }

    @Test
    fun registerUserTest_usernameTaken() {
        val request = ServiceRequest.RegistrationRequest(appId = "INVOICE_APP", username = "John Doe", password = "test1234", authorities = "USER, ADMIN")
        val result = registrationService.register(request)
        check(result.isLeft())
        check(result.fold({ customError -> customError is RegistrationFailure.UsernameAlreadyUsed }, { false }))
    }
}
