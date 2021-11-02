package com.ugisozols.routes.update_user

import com.google.common.truth.Truth.assertThat
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.di.mainModule
import com.ugisozols.routes.email
import com.ugisozols.routes.updateUser
import com.ugisozols.routes.util.JWTConfig

import com.ugisozols.service.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class UpdateUserRouteTest : KoinTest{

    private val userService by inject<UserService>()

    @BeforeTest
    fun setUp(){
        startKoin{
            modules(mainModule)
        }
    }

    @AfterTest
    fun tearDown(){
        stopKoin()
    }

    private fun initiateToken() = runBlocking {
        val user = CreateAccountRequest("TEST@TEST.COM", "123456789","123456789")
        userService.createUser(user)
//        JWTConfig.createToken(user)
    }

    @Test
    fun `Update user, check if request is null, should respond with unauthorized`(){

        withTestApplication(moduleFunction = {
            install(Authentication){
                jwt {
                    verifier(JWTConfig.verifier)
                    realm = "com.test"
                    validate { credentials ->
                        if(credentials.payload.audience.contains(JWTConfig.audience))
                            JWTPrincipal(credentials.payload) else null
                    }
                }
            }
            install(Routing){
                updateUser(userService)
            }

            }){
                initiateToken()
                val request = handleRequest {
                    method = HttpMethod.Post
                    uri = "api/user/profile/update"
                }
            assertThat(request.response.status()).isEqualTo(HttpStatusCode.Unauthorized)
        }
    }




}