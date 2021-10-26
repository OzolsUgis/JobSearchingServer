package com.ugisozols.plugins

import com.ugisozols.routes.createUser
import com.ugisozols.routes.getUserProfile
import com.ugisozols.routes.loginUser
import com.ugisozols.service.ProfileService
import com.ugisozols.service.UserService
import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()

    val userService : UserService by inject()
    val profileService : ProfileService by inject()


    routing {
        createUser(userService)
        loginUser(userService,issuer,audience,secret)
        getUserProfile(profileService)
    }
}
