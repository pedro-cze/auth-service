package cz.pedro.auth.controller

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.helper.FileLoader
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.security.model.AuthRequester
import cz.pedro.auth.service.AuthService
import cz.pedro.auth.service.TokenGenerationService
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
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    @Autowired
    lateinit var okHttpClient: OkHttpClient

    @Autowired
    lateinit var authService: AuthService

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
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_successful.json")
                            ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.OK.value())
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
        }

        @Test
        fun `login with wrong password`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_wrongPassword.json")
                            ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.UNAUTHORIZED.value())
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
            check(response.body()!!.string() == "Failure: Unauthorized")
        }

        @Test
        fun `login with empty password`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_emptyPassword.json")
                            ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.UNAUTHORIZED.value())
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
            check(response.body()!!.string() == "Failure: Unauthorized")
        }

        @Test
        fun `login with null password`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_nullPassword.json")
                            ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.BAD_REQUEST.value())
        }

        @Test
        fun `login with blank password`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_blankPassword.json")
                            ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.UNAUTHORIZED.value())
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
            check(response.body()!!.string() == "Failure: Unauthorized")
        }

        @Test
        fun `login with empty username`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_emptyUsername.json")
                            ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.UNAUTHORIZED.value())
            check(response.body()!!.contentType() == MediaType.parse("text/plain;charset=UTF-8"))
            check(response.body()!!.string() == "Failure: Null or empty username")
        }

        @Test
        fun `login with null username`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_nullUsername.json")
                            ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.BAD_REQUEST.value())
        }

        @Test
        fun `login with blank username`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("login/loginRequest_blankUsername.json")
                            ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:8083/auth/login"))
                    .build()
            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.UNAUTHORIZED.value())
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
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_successful.json")
                                ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.CREATED.value())
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
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_emptyPassword.json")
                                ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.BAD_REQUEST.value())
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
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_nullPassword.json")
                                ?: File("")))
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
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_blankPassword.json")
                                ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.BAD_REQUEST.value())
                check(response.body()!!.string() == "Failure: Null or empty password")
            }
            check(admin != null)
        }

        @Test
        fun `invalid authorities`() {
            val admin = userRepository.findByUsername("John Doe")
            admin?.let {
                val token = tokenGenerationService.generateToken(AuthRequester(admin))
                val request = Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_invalidAuthorities.json")
                                ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.BAD_REQUEST.value())
                check(response.body()!!.string() == "Failure: Request didn't pass validation")
            }
            check(admin != null)
        }

        @Test
        fun `login as admin authorities empty`() {
            val admin = userRepository.findByUsername("John Doe")
            admin?.let {
                val token = tokenGenerationService.generateToken(AuthRequester(admin))
                val request = Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_emptyAuthorities.json")
                                ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.CREATED.value())
                check(response.body()!!.string() == "John Soul")
            }
            check(admin != null)
        }

        @Test
        fun `login as admin authorities blank`() {
            val admin = userRepository.findByUsername("John Doe")
            admin?.let {
                val token = tokenGenerationService.generateToken(AuthRequester(admin))
                val request = Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_blankAuthorities.json")
                                ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.CREATED.value())
                check(response.body()!!.string() == "John Fitzgerald")
            }
            check(admin != null)
        }

        @Test
        fun `login as admin authorities null`() {
            val admin = userRepository.findByUsername("John Doe")
            admin?.let {
                val token = tokenGenerationService.generateToken(AuthRequester(admin))
                val request = Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_nullAuthorities.json")
                                ?: File("")))
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
        fun `username taken`() {
            val admin = userRepository.findByUsername("John Doe")
            admin?.let {
                val token = tokenGenerationService.generateToken(AuthRequester(admin))
                val request = Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_usernameTaken.json")
                                ?: File("")))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authentication", token)
                        .url(HttpUrl.get("http://localhost:8083/auth/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.BAD_REQUEST.value())
                check(response.body()?.string() == "Failure: Username already taken")
            }
            check(admin != null)
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
                            .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_successful.json")
                                    ?: File("")))
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

            init {
                authService.register(ServiceRequest.RegistrationRequest("John Doe", "Test1234", "ADMIN", true))
                authService.register(ServiceRequest.RegistrationRequest("Elliot Alderson", "Test1234", "USER", true))
            }

            @Test
            fun `successful patch setting new username`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("Elliot Alderson")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_updateUsernameSuccess.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.OK.value())
                    check(response.body() != null)
                    check(response.body()!!.string() == "Mr. Robot")
                    val updated = userRepository.findByUsername("Mr. Robot")
                    check(updated != null)
                    val previous = userRepository.findByUsername("Elliot Alderson")
                    check(previous == null)
                }
                check(admin != null)
            }

            @Test
            fun `successful patch setting new password`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_updatePasswordSuccess.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.OK.value())
                    check(response.body() != null)
                    check(response.body()!!.string() == "John Doe")
                }
                check(admin != null)
            }

            @Test
            fun `successful patch adding new authority`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_addingNewAuthority.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.OK.value())
                    check(response.body() != null)
                    check(response.body()!!.string() == "John Doe")

                    val updated = userRepository.findByUsername("John Doe")
                    check(updated != null && updated.authorities == "ADMIN, USER")
                }
                check(admin != null)
            }

            @Test
            fun `successful patch removing one authority`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_removeOneAuthority.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.OK.value())
                    check(response.body() != null)
                    check(response.body()!!.string() == "John Doe")

                    val updated = userRepository.findByUsername("John Doe")
                    check(updated != null && updated.authorities == "ADMIN")
                }
                check(admin != null)
            }

            @Test
            fun `successful patch removing all authorities`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_removeAllAuthorities.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.OK.value())
                    check(response.body() != null)
                    check(response.body()!!.string() == "John Doe")

                    val updated = userRepository.findByUsername("John Doe")
                    check(updated != null && updated.authorities == "")
                }
                check(admin != null)
            }

            @Test
            fun `successful patch setting null username`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_nullUsername.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.OK.value())
                    check(response.body()?.string() == "John Doe")
                    val updated = userRepository.findByUsername("John Doe")
                    check(updated?.username?.isNotEmpty() ?: false)
                    check(updated == admin)
                }
            }

            @Test
            fun `successful setting null password nothing should be changed`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_nullPassword.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.OK.value())
                    check(response.body()?.string() == "John Doe")

                    val updated = userRepository.findByUsername("John Doe")
                    check(updated?.password?.isNotEmpty() ?: false)
                    check(updated == admin)
                }
            }
        }

        @Nested
        inner class ErrorScenarios {

            init {
                authService.register(ServiceRequest.RegistrationRequest("John Doe", "Test1234", "ADMIN", true))
                authService.register(ServiceRequest.RegistrationRequest("Elliot Alderson", "Test1234", "USER", true))
            }

            @Test
            fun `error setting blank username`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_errorOnBlankUsername.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.BAD_REQUEST.value())
                    check(response.body()?.string() == "Failure: Empty username")
                }
            }

            @Test
            fun `error setting empty username`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_errorOnEmptyUsername.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.BAD_REQUEST.value())
                    check(response.body()?.string() == "Failure: Empty username")
                }
            }

            @Test
            fun `error setting already taken username`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_usernameTaken.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.BAD_REQUEST.value())
                    check(response.body()?.string() == "Failure: Username already taken")
                }
            }

            @Test
            fun `error setting an empty password`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_emptyPassword.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.BAD_REQUEST.value())
                    check(response.body()?.string() == "Failure: Empty password")

                    val updated = userRepository.findByUsername("John Doe")
                    check(updated?.password?.isNotEmpty() ?: false)
                }
            }

            @Test
            fun `error setting blank password`() {
                val admin = userRepository.findByUsername("John Doe")
                admin?.let {
                    val token = tokenGenerationService.generateToken(AuthRequester(admin))
                    val uuid: UUID? = userRepository.findByUsername("John Doe")?.id
                    val request = Request.Builder()
                            .patch(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("patch/patchRequest_settingBlankPassword.json")
                                    ?: File("")))
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authentication", token)
                            .url(HttpUrl.get("http://localhost:8083/auth/update/${uuid?.toString()}"))
                            .build()

                    val response = okHttpClient.newCall(request).execute()
                    check(response.code() == HttpStatus.BAD_REQUEST.value())
                    check(response.body()?.string() == "Failure: Empty password")

                    val updated = userRepository.findByUsername("John Doe")
                    check(updated?.password?.isNotBlank() ?: false)
                }
            }
        }
    }
}
