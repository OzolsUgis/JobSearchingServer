package com.ugisozols.util

import com.google.gson.Gson
import com.google.inject.Inject
import com.ugisozols.data.requests.AccountRequest
import com.ugisozols.data.responses.AuthResponse
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.routes.getUserPrivate
import com.ugisozols.routes.loginUser
import com.ugisozols.service.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*



fun loginIntoMockUser(userService: UserService, email : String) : AuthResponse {
    val gson = Gson()
    var authResponse : AuthResponse? = null
    withTestApplication {
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing) {
                    loginUser(userService, JWTConfig.issuer, JWTConfig.audience, JWTConfig.secret)
                }
            }
        ) {
            val requestedLogin = AccountRequest(
                email,
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
                loginRequest.response.content ?: "",
                MainApiResponse::class.java
            )
            authResponse = gson.fromJson(
                loginResponse.data.toString(),
                AuthResponse::class.java
            )
        }
    }
    return authResponse?: AuthResponse("","","")
}