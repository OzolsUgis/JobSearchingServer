package com.ugisozols.routes

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.data.responses.PublicAccountResponse
import com.ugisozols.di.fakeModule
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.service.UserService
import com.ugisozols.util.createMockUser
import com.ugisozols.util.getUsersId
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.litote.kmongo.MongoOperator
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



}