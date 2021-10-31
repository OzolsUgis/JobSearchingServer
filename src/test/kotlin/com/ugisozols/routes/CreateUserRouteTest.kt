package com.ugisozols.routes

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.ugisozols.data.models.Categories
import com.ugisozols.data.models.CurrentJobState
import com.ugisozols.data.models.Education
import com.ugisozols.data.models.User
import com.ugisozols.data.repository.user.FakeUserRepository
import com.ugisozols.data.repository.user.UserRepository
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.di.testModule
import com.ugisozols.plugins.configureRouting
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.service.UserService
import com.ugisozols.util.Constants.ERROR_EMAIL_ALREADY_EXISTS
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.litote.kmongo.json
import kotlin.test.*

internal class CreateUserRouteTest : KoinTest {


    private val userService by inject<UserService>()


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
}