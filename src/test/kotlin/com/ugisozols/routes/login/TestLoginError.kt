package com.ugisozols.routes.login

import com.google.common.truth.Truth
import com.google.gson.Gson
import com.typesafe.config.ConfigFactory
import com.ugisozols.data.requests.AccountRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.plugins.configureSerialization
import com.ugisozols.routes.loginUser
import com.ugisozols.service.UserService
import com.ugisozols.util.Constants
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*

private val configEnvironment = createTestEnvironment() {
    config = HoconApplicationConfig(ConfigFactory.load("application.conf"))

}
private val issuer = configEnvironment.config.property("jwt.issuer").getString()
private val secret = configEnvironment.config.property("jwt.secret").getString()
private val audience = configEnvironment.config.property("jwt.audience").getString()
private val gson = Gson()


fun testingLoginErrors(
    userService: UserService,
    loginRequest : AccountRequest,
    error : String
){
    withTestApplication(
        moduleFunction = {
            configureSerialization()
            install(Routing){
                loginUser(
                    userService,
                    issuer,
                    audience,
                    secret
                )
            }
        }
    ){
        val request =  handleRequest(
            method = HttpMethod.Post,
            uri = "api/userAuth/loginUser"
        ){
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(gson.toJson(loginRequest))
        }
        val response = gson.fromJson(
            request.response.content ?: "",
            MainApiResponse::class.java
        )
        Truth.assertThat(response.successful).isFalse()
        Truth.assertThat(response.message).isEqualTo(error)
    }
}