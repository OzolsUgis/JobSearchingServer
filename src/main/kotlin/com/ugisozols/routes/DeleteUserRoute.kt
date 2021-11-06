package com.ugisozols.routes

import com.ugisozols.data.requests.DeleteUserRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.service.UserService
import com.ugisozols.util.ApiResponses.USER_DELETED
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.deleteRoute(
    userService: UserService
){
    authenticate {
        post("api/users/delete") {
            val request = call.receiveOrNull<DeleteUserRequest>() ?: kotlin.run {
                call.respond(
                    HttpStatusCode.BadRequest
                )
                return@post
            }
            val userIdBelongsToToken = call.userId == request.userId
            if (userIdBelongsToToken){
                val userIsDeleted = userService.deleteUser(request.userId)
                if(userIsDeleted){
                    call.respond(
                        HttpStatusCode.OK,
                        MainApiResponse<Unit>(
                            true,
                            USER_DELETED
                        )
                    )
                }else{
                    call.respond(
                        HttpStatusCode.BadRequest,
                        MainApiResponse<Unit>(
                            false
                        )
                    )
                    return@post
                }
            }else{
                call.respond(
                    HttpStatusCode.Unauthorized
                )
                return@post
            }
        }
    }
}