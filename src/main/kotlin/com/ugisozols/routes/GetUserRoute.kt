package com.ugisozols.routes

import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.service.UserService
import com.ugisozols.util.QueryParameters
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getUserRoute(
    userService: UserService
){
    get("api/user/profile/get") {
        val query = call.parameters[QueryParameters.QUERY_PARAM_USER_ID]
        if(query == null || query.isBlank()){
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val publicProfile = userService.getUsersPublicProfile(query)
        call.respond(
            HttpStatusCode.OK,
            MainApiResponse(
                true,
                data = publicProfile
            )
        )
    }
}