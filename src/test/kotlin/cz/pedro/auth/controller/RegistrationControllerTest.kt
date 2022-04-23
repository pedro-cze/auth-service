package cz.pedro.auth.controller

import cz.pedro.auth.data.ServiceRequest
import cz.pedro.auth.helper.FileLoader
import cz.pedro.auth.repository.UserRepository
import cz.pedro.auth.security.model.AuthRequester
import cz.pedro.auth.service.RegistrationService
import cz.pedro.auth.service.TokenGenerationService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType
import okhttp3.HttpUrl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.io.File

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationControllerTest {

    @LocalServerPort
    lateinit var port: String

    @Autowired
    lateinit var registrationService: RegistrationService
    @Autowired
    lateinit var okHttpClient: OkHttpClient
    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var tokenGenerationService: TokenGenerationService

    @Nested
    inner class Registration {

        init {
            registrationService.register(ServiceRequest.RegistrationRequest("INVOICE_APP", "John Doe", "Test1234", "ADMIN"))
        }

        @Test
        fun `registration of a new user`() {
            val request = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("application/json"), FileLoader.loadFile("registration/registrationRequest_successful.json")
                            ?: File("")))
                    .addHeader("Content-Type", "application/json")
                    .url(HttpUrl.get("http://localhost:$port/signup/new"))
                    .build()

            val response = okHttpClient.newCall(request).execute()
            check(response.code() == HttpStatus.CREATED.value()) { response }
            check(response.body()!!.string() == "John Deer")
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
                        .url(HttpUrl.get("http://localhost:$port/signup/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.BAD_REQUEST.value()) { response.code() }
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
                        .url(HttpUrl.get("http://localhost:$port/signup/new"))
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
                        .url(HttpUrl.get("http://localhost:$port/signup/new"))
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
                        .url(HttpUrl.get("http://localhost:$port/signup/new"))
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
                        .url(HttpUrl.get("http://localhost:$port/signup/new"))
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
                        .url(HttpUrl.get("http://localhost:$port/signup/new"))
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
                        .url(HttpUrl.get("http://localhost:$port/signup/new"))
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
                        .url(HttpUrl.get("http://localhost:$port/signup/new"))
                        .build()

                val response = okHttpClient.newCall(request).execute()
                check(response.code() == HttpStatus.BAD_REQUEST.value())
                check(response.body()?.string() == "Failure: Username already taken")
            }
            check(admin != null)
        }
    }
}
