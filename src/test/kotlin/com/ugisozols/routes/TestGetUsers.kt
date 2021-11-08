package com.ugisozols.routes

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.ugisozols.data.requests.AccountRequest
import com.ugisozols.data.responses.AuthResponse
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.data.responses.PublicAccountResponse
import com.ugisozols.di.fakeModule
import com.ugisozols.plugins.configureSecurity
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.service.UserService
import com.ugisozols.util.JWTConfig
import com.ugisozols.util.JWTConfig.configureTestSecurity
import com.ugisozols.util.createMockUpdatedUser
import com.ugisozols.util.createMockUser
import com.ugisozols.util.getUsersId
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.json
import org.testng.reporters.jq.Main
import java.util.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestGetUsers : KoinTest {
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
    fun `Get public user, empty userId parameter, should respond with bad request`(){
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    getUserPublic(userService)
                }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Get,
                uri = "api/user/profile/public/get"
            )
            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun `Get public user, valid userId parameter, should respond user`(){
        createMockUser(userService)
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    getUserPublic(userService)
                }
            }
        ) {
            val userId = getUsersId(userService)
            val requestedQuery = "?userId=${userId?.id}"
            val request = handleRequest(
                method = HttpMethod.Get,
                uri = "api/user/profile/public/get$requestedQuery"
            )
            val response = gson.fromJson(
                request.response.content?: "",
                MainApiResponse::class.java
            )
            assertThat(request.response.status()).isEqualTo(HttpStatusCode.OK)
            assertThat(response.successful).isTrue()
            assertThat(response.data).isNotNull()
            println("Fake user got by id " + response.data)
        }
    }

    @Test
    fun `Get all users, should respond with list of users`(){
        createMockUpdatedUser(userService)
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    getAllUsers(userService)
                }
            }
        ) {
            val request = handleRequest(
                HttpMethod.Get,
                uri = "/api/user/getAllUsers"
            )
            val response = gson.fromJson(
                request.response.content?: "",
                MainApiResponse::class.java
            )
            assertThat(response.successful).isTrue()
            assertThat(response.data).isNotNull()
            print("List of Users" + response.data)
        }
    }

    @Test
    fun `Get private user, authentication and userId valid, should respond with call response`(){
        createMockUpdatedUser(userService)
        withTestApplication(
            moduleFunction ={
                install(Authentication){
                    configureTestSecurity()
                }
                configureSerialization()
                install(Routing) {
                    loginUser(userService, JWTConfig.issuer, JWTConfig.audience, JWTConfig.secret)
                    getUserPrivate(userService)

                }
        }
        ) {
            val requestedLogin = AccountRequest(
                "Test@test.com",
                "123456789"
            )
            val loginRequest = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/login"
            ) {
                addHeader(HttpHeaders.ContentType, "application/json")
                setBody(gson.toJson(requestedLogin))
                }
            val loginResponse = gson.fromJson(
                loginRequest.response.content?: "",
                MainApiResponse::class.java
                )
            val authResponse = gson.fromJson(
                loginResponse.data.toString(),
                AuthResponse::class.java
            )
            val requestedUserIdQuery = "?userId=${authResponse.userId}"

            val getUserRequest = handleRequest(
                method = HttpMethod.Get,
                uri = "api/user/profile/private/get$requestedUserIdQuery"
            ) {
                addHeader(HttpHeaders.Authorization, "Bearer ${authResponse.token}")
            }
            val userResponse = gson.fromJson(
                getUserRequest.response.content?: "",
                MainApiResponse::class.java
            )
            println(userResponse.data)
            assertThat(userResponse.successful).isTrue()

        }
    }

}