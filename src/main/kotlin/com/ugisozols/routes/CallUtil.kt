package com.ugisozols.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

val ApplicationCall.userId : String?
    get() = principal<JWTPrincipal>()?.getClaim("userId", String::class)

val ApplicationCall.email : String?
    get() = principal<JWTPrincipal>()?.getClaim("email", String::class)