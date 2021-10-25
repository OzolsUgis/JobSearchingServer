package com.ugisozols.plugins

import com.ugisozols.routes.createUser
import com.ugisozols.routes.loginUser
import com.ugisozols.service.UserService
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.content.*
import io.ktor.http.content.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.domain").getString()
    val audience = environment.config.property("jwt.audience").getString()

    val userService : UserService by inject()


    routing {
        createUser(userService)
        loginUser(userService,issuer,audience,secret)
    }
}
