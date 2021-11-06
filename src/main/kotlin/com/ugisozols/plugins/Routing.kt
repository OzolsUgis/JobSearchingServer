package com.ugisozols.plugins

import com.ugisozols.routes.*
import com.ugisozols.service.SortService
import com.ugisozols.service.UserService
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.content.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()

    val userService : UserService by inject()
    val sortService : SortService by inject()


    routing {
        createUser(userService)
        loginUser(userService,issuer,audience,secret)
        getUserPublic(userService)
        updateUser(userService)
        getUserPrivate(userService)
        getAllUsers(userService)
        sortByCategoryAndKeywords(sortService)

        static {
            resources("static")
        }
    }
}
