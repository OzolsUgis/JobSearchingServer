package com.ugisozols.plugins

import com.ugisozols.routes.createUser
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
    val userService : UserService by inject()

    routing {
        createUser(userService)
    }
}
