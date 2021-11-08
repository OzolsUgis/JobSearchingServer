package com.ugisozols.routes

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.ugisozols.data.requests.DeleteUserRequest
import com.ugisozols.di.fakeModule
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.service.UserService
import com.ugisozols.util.JWTConfig.configureTestSecurity
import com.ugisozols.util.createMockUpdatedUser
import com.ugisozols.util.createMockUser
import com.ugisozols.util.createSecondUser
import com.ugisozols.util.loginIntoMockUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.litote.kmongo.json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestDeleteUser : KoinTest {
    private val userService by inject<UserService>()
    private val gson = Gson()

    @BeforeTest
    fun setUp(){
        startKoin {
            modules(fakeModule)
        }
    }

    @AfterTest
    fun tearDown(){
        stopKoin()
    }

    @Test
    fun `Delete user, no body attached, should respond with bad request`(){
        createMockUser(userService)
        val login = loginIntoMockUser(userService, "Test@test.com")
        withTestApplication(
            moduleFunction = {
                install(Authentication){
                    configureTestSecurity()
                }
                configureSerialization()
                install(Routing){
                    deleteRoute(userService)
                }
            }
        ) {
            val deleteRequest = handleRequest(
                method = HttpMethod.Post,
                uri = "api/users/delete"
            ){
                addHeader(HttpHeaders.Authorization, "Bearer ${login.token}")
            }
            assertThat(deleteRequest.response.status()).isEqualTo(HttpStatusCode.BadRequest)

        }
    }

    @Test
    fun `Delete user, user in not authorized to delete requested user, should respond with unauthorized`(){
        createMockUpdatedUser(userService)
        createSecondUser(userService)
        val login = loginIntoMockUser(userService, "Test@test.com")
        val secondUserlogin = loginIntoMockUser(userService, "Username@test.com")
        withTestApplication(
            moduleFunction = {
                install(Authentication){
                    configureTestSecurity()
                }
                configureSerialization()
                install(Routing){
                    deleteRoute(userService)
                }
            }
        ) {
            val requestUserId = DeleteUserRequest(
                secondUserlogin.userId
            )
            val requestToDelete = handleRequest(
                method = HttpMethod.Post,
                uri = "api/users/delete"
            ) {
                addHeader(HttpHeaders.Authorization, "Bearer ${login.token}")
                addHeader(HttpHeaders.ContentType, "application/json")
                setBody(gson.toJson(requestUserId))
            }
            assertThat(requestToDelete.response.status()).isEqualTo(HttpStatusCode.Unauthorized)

        }
    }


}