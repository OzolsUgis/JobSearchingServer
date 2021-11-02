package com.ugisozols.routes.login

import com.google.common.truth.Truth
import com.google.gson.Gson
import com.typesafe.config.ConfigFactory
import com.ugisozols.data.requests.AccountRequest
import com.ugisozols.di.testModule
import com.ugisozols.routes.loginUser
import com.ugisozols.service.UserService
import com.ugisozols.util.Constants
import com.ugisozols.util.Constants.ERROR_PASSWORD_OR_EMAIL_INCORRECT
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

internal class LoginUserRouteTest: KoinTest{

    private val userService by inject<UserService>()
    private val gson = Gson()
    private val configEnvironment = createTestEnvironment() {
        config = HoconApplicationConfig(ConfigFactory.load("application.conf"))

    }
    private val issuer = configEnvironment.config.property("jwt.issuer").getString()
    private val secret = configEnvironment.config.property("jwt.secret").getString()
    private val audience = configEnvironment.config.property("jwt.audience").getString()

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
                        issuer,
                        audience,
                        secret
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


}