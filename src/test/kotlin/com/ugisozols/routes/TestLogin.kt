package com.ugisozols.routes

import com.google.common.truth.Truth.assertThat
import com.typesafe.config.ConfigFactory
import com.ugisozols.di.fakeModule
import com.ugisozols.service.UserService
import com.ugisozols.util.JWTConfig
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
import org.junit.Test

internal class TestLogin : KoinTest {
    private val userService by inject<UserService>()


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


}