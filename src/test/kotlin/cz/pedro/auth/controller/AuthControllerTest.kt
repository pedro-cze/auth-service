package cz.pedro.auth.controller

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.helper.FileLoader
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.security.model.AuthRequester
import cz.pedro.auth.service.AuthService
import cz.pedro.auth.service.TokenGenerationService
import cz.pedro.auth.service.ValidationService
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.io.File

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    @Autowired
    lateinit var okHttpClient: OkHttpClient
    @Autowired
    lateinit var authController: AuthController
    @Autowired
    lateinit var authService: AuthService
    @Autowired
    lateinit var validationService: ValidationService
    @Autowired
    lateinit var tokenGenerationService: TokenGenerationService
    @Autowired
    lateinit var userRepository: UserRepository

    @Nested
    inner class Login {

        init {
            authService.register(ServiceRequest.RegistrationRequest("John Doe", "Test1234", "ADMIN", true))
        }

        @Test
        fun `successful login`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_successful.json") ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.OK.value())
            check(response.body() != null)
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
        }
        @Test
        fun `login with wrong password`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_wrongPassword.json") ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.UNAUTHORIZED.value())
            check(response.body() != null)
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
            check(response.body()!!.string() == "Failure: Unauthorized")
        }
        @Test
        fun `login with empty password`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_emptyPassword.json") ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.UNAUTHORIZED.value())
            check(response.body() != null)
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
            check(response.body()!!.string() == "Failure: Unauthorized")
        }
        @Test
        fun `login with null password`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_nullPassword.json") ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.BAD_REQUEST.value())
        }
        @Test
        fun `login with blank password`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_blankPassword.json") ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.UNAUTHORIZED.value())
            check(response.body() != null)
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
            check(response.body()!!.string() == "Failure: Unauthorized")
        }
        @Test
        fun `login with empty username`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_emptyUsername.json") ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.UNAUTHORIZED.value())
            check(response.body() != null)
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
            check(response.body()!!.string() == "Failure: Null or empty username")
        }
        @Test
        fun `login with null username`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_nullUsername.json") ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.BAD_REQUEST.value())
        }
        @Test
        fun `login with blank username`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_blankUsername.json") ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.UNAUTHORIZED.value())
            check(response.body() != null)
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
            check(response.body()!!.string() == "Failure: Null or empty username")
        }
    }
    @Nested
    inner class Registration {

        init {
            authService.register(ServiceRequest.RegistrationRequest("John Doe", "Test1234", "ADMIN", true))
        }

        @Test
        fun `login as admin and successful registration`() {
            val admin = userRepository.findByUsername("John Doe")
            admin?.let {
                val token = tokenGenerationService.generateToken(AuthRequester(admin))
                val request = Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_successful.json") ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.CREATED.value())
                check(response.body() != null)
                check(response.body()!!.string() == "John Deer")
            }
            check(admin != null)
        }
        @Test
        fun `login as admin and empty password`() {
            val admin = userRepository.findByUsername("John Doe")
            admin?.let {
                val token = tokenGenerationService.generateToken(AuthRequester(admin))
                val request = Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_emptyPassword.json") ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.BAD_REQUEST.value())
                check(response.body() != null)
                check(response.body()!!.string() == "Failure: Null or empty password")
            }
            check(admin != null)
        }
        @Test
        fun `login as admin and null password`() {
            val admin = userRepository.findByUsername("John Doe")
            admin?.let {
                val token = tokenGenerationService.generateToken(AuthRequester(admin))
                val request = Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_nullPassword.json") ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.BAD_REQUEST.value())
            }
            check(admin != null)
        }
        @Test
        fun `login as admin and blank password`() {
            val admin = userRepository.findByUsername("John Doe")
            admin?.let {
                val token = tokenGenerationService.generateToken(AuthRequester(admin))
                val request = Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_blankPassword.json") ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.BAD_REQUEST.value())
                check(response.body() != null)
                check(response.body()!!.string() == "Failure: Null or empty password")
            }
            check(admin != null)
        }
        @Test
        fun `invalid authorities`() {
        }
        @Test
        fun `username taken`() {
        }

        @Nested
        inner class RegisterAsUser {

            init {
                authService.register(ServiceRequest.RegistrationRequest("John User", "Test1234", "USER", true))
            }

            @Test
            fun `login as user and try to register a user`() {
                val user = userRepository.findByUsername("John User")
                user?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(user))
                    val request = Request.Builder()
                            .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_successful.json") ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/new"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.FORBIDDEN.value())
                }
                check(user != null)
            }
        }
    }
    @Nested
    inner class UserPatch {
        @Nested
        inner class SuccessfulScenarios {
            @Test
            fun `successful patch setting new username`() {}
            @Test
            fun `successful patch setting new password`() {}
            @Test
            fun `successful patch adding new authority`() {}
            @Test
            fun `successful patch removing one authority`() {}
            @Test
            fun `successful patch removing all authorities`() {}
        }
        @Nested
        inner class ErrorScenarios {
            @Test
            fun `error setting already taken username`() {}
            @Test
            fun `error setting an empty password`() {}
            @Test
            fun `error setting null password`() {}
        }
        @Nested
        inner class OtherScenarios {
            @Test
            fun `test add a very long username`() {}
            @Test
            fun `test add a very long password`() {}
            @Test
            fun `test add a very long list of authorities`() {}
            @Test
            fun `test add some random string for authorities`() {}
        }
    }
}
