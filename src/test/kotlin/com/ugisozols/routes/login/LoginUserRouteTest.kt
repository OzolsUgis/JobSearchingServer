package com.ugisozols.routes.login

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.typesafe.config.ConfigFactory
import com.ugisozols.data.repository.user.FakeUserRepository
import com.ugisozols.data.requests.AccountRequest
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.data.responses.AuthResponse
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.di.testModule
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.routes.loginUser
import com.ugisozols.routes.util.JWTConfig
import com.ugisozols.service.UserService
import com.ugisozols.util.Constants
import com.ugisozols.util.Constants.ERROR_PASSWORD_OR_EMAIL_INCORRECT
import com.ugisozols.util.createUser
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

internal class LoginUserRouteTest: KoinTest{

    private val userService by inject<UserService>()
    private val gson = Gson()


    @BeforeTest
    fun setUp(){
        startKoin {
            modules(testModule)
        }
    }


    @AfterTest
    fun tearDown(){
        stopKoin()
    }


    @Test
    fun `Login, no body attached, responds with badRequest`(){
        withTestApplication(
            moduleFunction = {
                install(Routing){
                    loginUser(
                        userService = userService,
                        JWTConfig.issuer,
                        JWTConfig.audience,
                        JWTConfig.secret
                    )
                }

            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/userAuth/loginUser"
            )
            Truth.assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun `Login, field empty, responds with unsuccessful`(){
        val loginRequest =
            AccountRequest (
                "Test@test.com",
                ""
            )
        testingLoginErrors(
            userService,
            loginRequest,
            Constants.ERROR_FIELDS_EMPTY
        )
    }

    @Test
    fun `Login, email or password incorrect, responds with unsuccessful`(){
        val loginRequest =
            AccountRequest(
                email = "Test@test.com",
                password = "123456789"
            )
        testingLoginErrors(
            userService,
            loginRequest,
            ERROR_PASSWORD_OR_EMAIL_INCORRECT
        )
    }

    @Test
    fun `Login, valid information, should respond with success`()= runBlocking{
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    loginUser(userService,
                    JWTConfig.issuer,
                    JWTConfig.audience,
                    JWTConfig.secret
                    )
                }
            }
        ) {
            var loginRequest: AccountRequest
            val request = handleRequest(
                HttpMethod.Post,
                uri = "api/userAuth/loginUser"
            ){
                 loginRequest = AccountRequest(
                    email = "Test@test.com",
                    password = "123456789"
                )
                val token = getToken(loginRequest)

                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader("Authorization", "Bearer $token")
                setBody(gson.toJson(loginRequest))
            }
            val response = gson.fromJson(
                request.response.content?: "",
                MainApiResponse::class.java
            )
            assertThat(response.successful).isTrue()
        }
    }


    private fun getToken(user: AccountRequest){
        JWTConfig.createToken(user)
    }

}