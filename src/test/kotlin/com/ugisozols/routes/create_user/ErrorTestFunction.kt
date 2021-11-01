package com.ugisozols.routes.create_user

import com.google.common.truth.Truth
import com.google.gson.Gson
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.routes.createUser
import com.ugisozols.service.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*


fun errorTestFunction(
    callRequest : CreateAccountRequest,
    error : String,
    userService : UserService,
    gson : Gson = Gson()
){
    withTestApplication(
        moduleFunction = {
            configureSerialization()
            install(Routing){
                createUser(userService)
            }
        }
    ) {
        val request = handleRequest(
            HttpMethod.Post,
            uri = "/api/userAuth/createUser"
        ) {
            addHeader(HttpHeaders.ContentType,"application/json")
            setBody(gson.toJson(callRequest))
        }
        val response = gson.fromJson(
            request.response.content ?: "",
            MainApiResponse::class.java
        )
        Truth.assertThat(response.successful).isFalse()
        Truth.assertThat(response.message).isEqualTo(error)
    }
}