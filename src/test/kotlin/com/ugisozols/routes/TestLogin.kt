package com.ugisozols.routes

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.ugisozols.data.requests.AccountRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.di.fakeModule
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.service.UserService
import com.ugisozols.util.ApiResponses.ERROR_FIELDS_EMPTY
import com.ugisozols.util.ApiResponses.ERROR_PASSWORD_OR_EMAIL_INCORRECT
import com.ugisozols.util.JWTConfig
import com.ugisozols.util.createMockUser
import com.ugisozols.util.testError
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import org.junit.Test

internal class TestLogin : KoinTest {
    private val userService by inject<UserService>()
    private val gson = Gson()


    @BeforeTest
    fun setUp() {
        startKoin {
            modules(fakeModule)
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `Login, no body attached, should respond with bad request`(){
        withTestApplication(
            moduleFunction = {
                install(Routing){
                    loginUser(userService, JWTConfig.issuer,JWTConfig.audience,JWTConfig.secret)
                }
            }
        ) {
            val request = handleRequest {
                method = HttpMethod.Post
                uri = "api/user/login"
            }
            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun `Login, fields empty, should respond with error`(){
        val request = AccountRequest(
            email = "",
            password = "123456789"
        )
        testError(
            route = {
                loginUser(userService, JWTConfig.issuer,JWTConfig.audience,JWTConfig.secret)
            },
            method = HttpMethod.Post,
            uri = "api/user/login",
            requestedUser = request,
            error = ERROR_FIELDS_EMPTY
        )
    }

    @Test
    fun `Login, user email is not found , should respond with error`(){
        val requestedUser = AccountRequest(
            email = "randomString",
            password = "123456789"
        )
        testError(
            route = {
                loginUser(userService, JWTConfig.issuer,JWTConfig.audience,JWTConfig.secret)
            },
            method = HttpMethod.Post,
            uri = "api/user/login",
            requestedUser = requestedUser,
            error = ERROR_PASSWORD_OR_EMAIL_INCORRECT
        )
    }

    @Test
    fun `Login, password incorrect, should respond with error`(){
        createMockUser(userService)
        val request = AccountRequest(
            email = "Test@test.com",
            password = "randomPassword"
        )
        testError(
            route ={
                loginUser(userService, JWTConfig.issuer,JWTConfig.audience,JWTConfig.secret)
            },
            method = HttpMethod.Post,
            uri = "api/user/login",
            requestedUser = request,
            error = ERROR_PASSWORD_OR_EMAIL_INCORRECT

        )


    }

    @Test
    fun `Login, valid credentials, should respond with successful and token`(){
        createMockUser(userService)
        val requestedUser = AccountRequest(
            email = "Test@test.com",
            password = "123456789"
        )
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    loginUser(userService,JWTConfig.issuer,JWTConfig.audience,JWTConfig.secret)
                }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/login"
            ) {
                addHeader(HttpHeaders.ContentType, "application/json")
                setBody(gson.toJson(requestedUser))
            }
            val response = gson.fromJson(
                request.response.content?: "",
                MainApiResponse::class.java
            )

            val data = response.data.toString()
            val separatedToken = data.substringAfterLast('=')
            val token = separatedToken.substring(0,separatedToken.length-1)
            println(token)
            assertThat(response.successful).isTrue()
            assertThat(response.data).isNotNull()

        }

    }
}