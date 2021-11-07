package com.ugisozols.util

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.plugins.configureSerialization
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*


fun testError(
    route: Routing.() -> Unit,
    method : HttpMethod,
    uri : String,
    requestedUser : Any,
    error : String
){
    val gson = Gson()
    withTestApplication(
        moduleFunction = {
            configureSerialization()
            install(Routing){
                route()
            }
        }
    ) {
        val request = handleRequest(
            method = method,
            uri = uri
        ){
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(gson.toJson(requestedUser))
        }
        val response = gson.fromJson(
            request.response.content?: "",
            MainApiResponse::class.java
        )
        assertThat(response.successful).isFalse()
        assertThat(response.message).isEqualTo(error)
    }

}