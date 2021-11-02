package com.ugisozols.routes

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.typesafe.config.ConfigFactory
import com.ugisozols.data.requests.AccountRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.di.testModule
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.service.UserService
import com.ugisozols.util.Constants
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test


internal class AuthRoutesKtTest : KoinTest {

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
            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun `Login, field empty, responds with unsuccessful`(){
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    loginUser(
                        userService,
                        issuer,
                        audience,
                        secret
                    )
                }
            }
        ){
            val request =  handleRequest(
                method = HttpMethod.Post,
                uri = "api/userAuth/loginUser"
            ){
                val loginRequest = AccountRequest(
                    "Test@test.com",
                    ""
                )
                addHeader(HttpHeaders.ContentType, "application/json")
                setBody(gson.toJson(loginRequest))
            }
            val response = gson.fromJson(
                request.response.content ?: "",
                MainApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(Constants.ERROR_FIELDS_EMPTY)
        }
    }



}