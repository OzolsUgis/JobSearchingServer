package com.ugisozols.routes

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.data.responses.UserListResponse
import com.ugisozols.di.fakeModule
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.service.UserService
import com.ugisozols.util.ApiResponses
import com.ugisozols.util.ApiResponses.ERROR_EMPTY_CATEGORY
import com.ugisozols.util.createMockUpdatedUser
import io.ktor.application.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.litote.kmongo.month
import java.util.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestSortUsersByCategory : KoinTest {
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
    fun`Get users by category, empty categories query parameter, should respond with bad request`(){
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    sortByCategoryAndKeywords(userService)
                }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Get,
                uri = "api/users/getUserByCategory"
            )
            val response = gson.fromJson(
                request.response.content?: "",
                MainApiResponse::class.java
            )
            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
            assertThat(response.message).isEqualTo(ERROR_EMPTY_CATEGORY)
        }
    }

    @Test
    fun `Get users by category, empty keywords query parameter , should respond with users sorted by category`(){
        createMockUpdatedUser(userService)
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    sortByCategoryAndKeywords(userService)
                }
            }
        ) {
            val requestedQuery = "?category=test"
            val request = handleRequest(
                method = HttpMethod.Get,
                uri = "api/users/getUserByCategory${requestedQuery}"
            )
            val response = gson.fromJson(
                request.response.content?: "",
                MainApiResponse::class.java
            )
            assertThat(response.successful).isTrue()
            assertThat(response.data).isNotNull()
        }
    }


    // Test should respond empty list - Keyword in parameter don't belong to any fakeUser
    @Test
    fun `Get Users by category, list sorted by keywords, should respond with empty list`(){
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing){
                    sortByCategoryAndKeywords(userService)
                }
            }
        ) {
            val queryRequest = "?category=test&keywords=Test"
            val request = handleRequest(
                method = HttpMethod.Get,
                uri = "api/users/getUserByCategory${queryRequest}"
            )
            val response = gson.fromJson(
                request.response.content?:"",
                MainApiResponse::class.java
            )
            assertThat(response.successful).isTrue()
            assertThat(response.data).isEqualTo(Collections.EMPTY_LIST)

        }
    }
}