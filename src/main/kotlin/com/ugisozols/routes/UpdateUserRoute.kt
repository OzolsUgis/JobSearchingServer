package com.ugisozols.routes

import com.ugisozols.data.models.User
import com.ugisozols.data.requests.ProfileUpdateRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.service.UserService
import com.ugisozols.util.Constants
import com.ugisozols.util.Constants.ERROR_ACCESS_DENIED
import com.ugisozols.util.QueryParameters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import sun.applet.Main

fun Route.updateUser(
    userService: UserService
){
    authenticate {
        post("api/user/profile/update") {
            val request = call.receiveOrNull<ProfileUpdateRequest>()?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val calledUserId = call.parameters[QueryParameters.QUERY_PARAM_USER_ID]
            if(calledUserId == null || calledUserId.isBlank()){
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if(!userService.checkIfUsersIdIsEqualToProfileId(calledUserId ?: "",call.userId?: "")){
                call.respond(
                    HttpStatusCode.Unauthorized,
                    MainApiResponse<Unit>(
                        false,
                        ERROR_ACCESS_DENIED
                    )
                )
                return@post
            }
            if(userService.updateUser(call.userId?: "", request)) {
                call.respond(
                    HttpStatusCode.OK,
                    MainApiResponse<Unit>(
                        true,
                        "Account successfully updated"
                    )
                )
            }
        }
    }
}