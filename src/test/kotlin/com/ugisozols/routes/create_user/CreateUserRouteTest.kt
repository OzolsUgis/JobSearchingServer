package com.ugisozols.routes.create_user

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.di.testModule
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.routes.createUser
import com.ugisozols.service.UserService
import com.ugisozols.util.Constants
import com.ugisozols.util.Constants.ERROR_EMAIL_ALREADY_EXISTS
import com.ugisozols.util.Constants.ERROR_EMAIL_DOES_NOT_CONTAIN_EMAIL_CHARS
import com.ugisozols.util.Constants.ERROR_FIELDS_EMPTY
import com.ugisozols.util.Constants.ERROR_PASSWORDS_DO_NOT_MATCH
import com.ugisozols.util.Constants.ERROR_PASSWORD_IS_TOO_SHORT
import com.ugisozols.util.createUser
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import sun.applet.Main
import kotlin.test.*

internal class CreateUserRouteTest : KoinTest {


    private val userService by inject<UserService>()
    private val gson = Gson()


    @BeforeTest
    fun setup(){
        startKoin {
            modules(testModule)
        }
    }

    @AfterTest
    fun teardown(){
        stopKoin()
    }

    @Test
    fun `Create user, no body attached, responds with badRequest`(){
        withTestApplication(
            moduleFunction = {
                install(Routing){
                    createUser(userService)
                }
        }
        ){
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/userAuth/createUser"
            )
            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun `Create user, email already exists, responds with unsuccessful`(){
        createUser(userService)
        val requestCall = CreateAccountRequest(
            "Test@test.com",
            "123456789",
            "123456789"
        )
        errorTestFunction(
            callRequest = requestCall,
            error = ERROR_EMAIL_ALREADY_EXISTS,
            userService
        )
    }

    @Test
    fun `Create user, field empty, responds with unsuccessful`(){
        val requestCall = CreateAccountRequest(
            "",
            "123456789",
            "123456789"
        )
        errorTestFunction(
            requestCall,
            ERROR_FIELDS_EMPTY,
            userService
        )
    }

    @Test
    fun `Create user, not containing specific chars, responds with unsuccessful`(){
        val requestCall = CreateAccountRequest(
            "username",
            "123456789",
            "123456789"
        )
        errorTestFunction(
            requestCall,
            ERROR_EMAIL_DOES_NOT_CONTAIN_EMAIL_CHARS,
            userService
        )
    }

    @Test
    fun `Create user, passwords do not match, responds with unsuccessful`(){
        val requestCall = CreateAccountRequest(
            "Test@test.com",
            "ThisIsTest",
            "123456789"
        )
        errorTestFunction(
            requestCall,
            ERROR_PASSWORDS_DO_NOT_MATCH,
            userService
        )
    }

    @Test
    fun `Create user, password is too short, responds with unsuccessful`(){
        val requestCall = CreateAccountRequest(
            "Test@test.com",
            "12345",
            "12345"
        )
        errorTestFunction(
            requestCall,
            ERROR_PASSWORD_IS_TOO_SHORT,
            userService
        )
    }

    @Test
    fun`Create user, valid request, responds with successful`(){
        withTestApplication(moduleFunction = {
            configureSerialization()
            install(Routing){
                createUser(userService)
            }
        }
        ) {
            val request = handleRequest(
                HttpMethod.Post,
                "api/userAuth/createUser"
            ){
                addHeader(HttpHeaders.ContentType, "application/json")
                val requestCall = CreateAccountRequest(
                    "test@test.com",
                    "123456789",
                    "123456789"
                )
                setBody(gson.toJson(requestCall))
            }
            val response = gson.fromJson(
                request.response.content?:"",
                MainApiResponse::class.java
            )
            assertThat(response.successful).isTrue()

            runBlocking {
                val userInDatabase = userService.getUserByEmail("test@test.com") != null
                assertThat(userInDatabase).isTrue()
            }
        }
    }
}