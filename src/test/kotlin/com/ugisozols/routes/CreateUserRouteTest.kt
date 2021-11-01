package com.ugisozols.routes

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.ugisozols.data.repository.user.FakeUserRepository
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.di.testModule
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.service.UserService
import com.ugisozols.util.Constants.ERROR_EMAIL_ALREADY_EXISTS
import com.ugisozols.util.Constants.ERROR_FIELDS_EMPTY
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
              "/api/userAuth/createUser"
          ){
              addHeader(HttpHeaders.ContentType,"application/json")
              val request = CreateAccountRequest(
                  email = "Test@test.com",
                  password = "123456789",
                  confirmedPassword = "123456789"
              )
              setBody(gson.toJson(request))
          }
            val response = gson.fromJson(
                request.response.content ?: "",
                MainApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(ERROR_EMAIL_ALREADY_EXISTS)

        }
    }

    @Test
    fun `Create user, field empty, responds with unsuccessful`(){
        createUser(userService)
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    createUser(userService)
                }
            }
        ) {
            val testUserCall = CreateAccountRequest(
                "",
                "123456789",
                "123456789"
            )
            val request = handleRequest(
                HttpMethod.Post,
                uri = "/api/userAuth/createUser"
            ) {
                addHeader(HttpHeaders.ContentType,"application/json")
                setBody(gson.toJson(testUserCall))
            }
            val response = gson.fromJson(
                request.response.content ?: "",
                MainApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(ERROR_FIELDS_EMPTY)
        }
    }
}