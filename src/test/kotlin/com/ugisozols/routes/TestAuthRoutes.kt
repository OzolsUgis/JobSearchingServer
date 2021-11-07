package com.ugisozols.routes

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.di.fakeModule
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.service.UserService
import com.ugisozols.util.ApiResponses.ERROR_EMAIL_ALREADY_EXISTS
import com.ugisozols.util.ApiResponses.ERROR_FIELDS_EMPTY
import com.ugisozols.util.createMockUser
import com.ugisozols.util.testError
import io.ktor.application.*
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



internal class TestAuthRoutes : KoinTest {
    private val userService by inject<UserService>()
    private val gson : Gson = Gson()

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
    fun `Create User, no body attached, should respond with bad request `(){
        withTestApplication(
            moduleFunction = {
                install(Routing){
                    createUser(userService)
                }
            }
        ) {
            val request = handleRequest {
                method = HttpMethod.Post
                uri = "api/user/create"
            }
            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun `Create User, email already exists, should respond with error`(){
        createMockUser(userService)
        val requestedUser = CreateAccountRequest(
            email = "Test@test.com",
            password = "123456789",
            confirmedPassword = "123456789"
        )
        testError(
            route = { createUser(userService)},
            method = HttpMethod.Post,
            uri = "api/user/create",
            requestedUser = requestedUser,
            error = ERROR_EMAIL_ALREADY_EXISTS
        )
    }

    @Test
    fun `Create user, field empty, should respond with error`(){
        val request = CreateAccountRequest(
            email = "",
            password = "123456789",
            confirmedPassword = "123456789"
        )
        testError(
            route = {createUser(userService)},
            method = HttpMethod.Post,
            uri = "api/user/create",
            requestedUser = request,
            error = ERROR_FIELDS_EMPTY
        )
    }

    @Test
    fun `Create user, valid data, responds with successful`(){
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    createUser(userService)
                }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/create"
            ) {
                addHeader(HttpHeaders.ContentType, "application/json")
                val request = CreateAccountRequest(
                    email ="Test@test.com",
                    password = "123456789",
                    confirmedPassword= "123456789"
                )
                setBody(gson.toJson(request))
            }

            val response = gson.fromJson(
                request.response.content ?: "",
                MainApiResponse::class.java
            )
            assertThat(response.successful).isTrue()
        }
    }
}