package com.ugisozols.routes

import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.service.UserService
import com.ugisozols.util.ApiResponses
import com.ugisozols.util.QueryParameters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.getUserPublic(
    userService: UserService
){
    get("api/user/profile/public/get") {
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

fun Route.getUserPrivate(
    userService: UserService
){
    authenticate {
        get("api/user/profile/private/get") {
            val userId = call.parameters[QueryParameters.QUERY_PARAM_USER_ID]
            if (userId == null || userId.isBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val callResponse = userService.getUsersProfile(userId = userId)
            if (callResponse == null) {
                call.respond(
                    HttpStatusCode.OK,
                    MainApiResponse<Unit>(
                        false,
                        ApiResponses.ERROR_USER_NOT_FOUND
                    )
                )
                return@get
            }
            val userIdIsLoggedInUsersId = userService
                .checkIfUsersIdIsEqualToProfileId(callResponse.id,call.userId ?: "")

            if (!userIdIsLoggedInUsersId) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    MainApiResponse<Unit>(
                        false,
                        ApiResponses.ERROR_ACCESS_DENIED
                    )
                )
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                MainApiResponse(
                    successful = true,
                    data = callResponse
                )
            )

        }
    }
}

fun Route.getAllUsers(
    userService: UserService
){
    get("/api/user/getAllUsers"){
        val listOfUsers = userService.getAllUsers()
        call.respond(
            HttpStatusCode.OK,
            MainApiResponse(
                true,
                data = listOfUsers
            )
        )
    }
}