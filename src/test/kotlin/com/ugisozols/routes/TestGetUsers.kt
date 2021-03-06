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
import com.ugisozols.util.*
import com.ugisozols.util.ApiResponses.ERROR_ACCESS_DENIED
import com.ugisozols.util.ApiResponses.ERROR_USER_NOT_FOUND
import com.ugisozols.util.JWTConfig.configureTestSecurity
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
        val login = loginIntoMockUser(userService, "Test@test.com")
        withTestApplication(
            moduleFunction ={
                install(Authentication){
                    configureTestSecurity()
                }
                configureSerialization()
                install(Routing) {
                    getUserPrivate(userService)
                }
        }
        ) {
            val requestedUserIdQuery = "?userId=${login.userId}"
            val getUserRequest = handleRequest(
                method = HttpMethod.Get,
                uri = "api/user/profile/private/get$requestedUserIdQuery"
            ) {
                addHeader(HttpHeaders.Authorization, "Bearer ${login.token}")
            }
            val userResponse = gson.fromJson(
                getUserRequest.response.content?: "",
                MainApiResponse::class.java
            )
            println(userResponse.data)
            assertThat(userResponse.successful).isTrue()

        }
    }

    @Test
    fun`Get private user, userId query is not attached, should respond with bad request`(){
        createMockUpdatedUser(userService)
        val login = loginIntoMockUser(userService,"Test@test.com")
        withTestApplication(
            moduleFunction ={
                install(Authentication){
                    configureTestSecurity()
                }
                configureSerialization()
                install(Routing) {
                    getUserPrivate(userService)
                }
            }
        ){
            val getUserRequest = handleRequest(
                method = HttpMethod.Get,
                uri = "api/user/profile/private/get"
            ) {
                addHeader(HttpHeaders.Authorization, "Bearer ${login.token}")
            }
            assertThat(getUserRequest.response.status()).isEqualTo(HttpStatusCode.BadRequest)

        }

    }

    @Test
    fun `Get private user, userId do not exist, should respond with user not found error`() {
        createMockUpdatedUser(userService)
        val login = loginIntoMockUser(userService,"Test@test.com")
        withTestApplication(
            moduleFunction = {
                install(Authentication) {
                    configureTestSecurity()
                }
                configureSerialization()
                install(Routing) {
                    getUserPrivate(userService)
                }
            }
        ) {
            val randomUserId = "Username"
            val requestedUserIdQuery = "?userId=$randomUserId"
            val getUserRequest = handleRequest(
                method = HttpMethod.Get,
                uri = "api/user/profile/private/get$requestedUserIdQuery"
            ) {
                addHeader(HttpHeaders.Authorization, "Bearer ${login.token}")
            }
            val userResponse = gson.fromJson(
                getUserRequest.response.content ?: "",
                MainApiResponse::class.java
            )
            assertThat(userResponse.successful).isFalse()
            assertThat(userResponse.message).isEqualTo(ERROR_USER_NOT_FOUND)
        }
    }

    @Test
    fun `Get private user, user is not authorized to requested userId, should respond with unauthorized`(){
        createMockUpdatedUser(userService)
        createSecondUser(userService)
        val login = loginIntoMockUser(userService, "Test@test.com")
        val secondUserlogin = loginIntoMockUser(userService, "Username@test.com")
        withTestApplication(
            moduleFunction = {
                install(Authentication) {
                    configureTestSecurity()
                }
                configureSerialization()
                install(Routing) {
                    getUserPrivate(userService)
                }
            }
        ) {
            val requestedUserIdQuery = "?userId=${secondUserlogin.userId}"
            val getUserRequest = handleRequest(
                method = HttpMethod.Get,
                uri = "api/user/profile/private/get$requestedUserIdQuery"
            ) {
                addHeader(HttpHeaders.Authorization, "Bearer ${login.token}")
            }
            val userResponse = gson.fromJson(
                getUserRequest.response.content ?: "",
                MainApiResponse::class.java
            )
            assertThat(userResponse.successful).isFalse()
            assertThat(userResponse.message).isEqualTo(ERROR_ACCESS_DENIED)
        }
    }

}