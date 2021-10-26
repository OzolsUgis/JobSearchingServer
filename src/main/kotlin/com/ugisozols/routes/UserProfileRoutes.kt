package com.ugisozols.routes
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.service.ProfileService
import com.ugisozols.util.Constants.ERROR_USER_NOT_FOUND
import com.ugisozols.util.QueryParameters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.getUserProfile(
    profileService: ProfileService
){
    authenticate {
        get("api/user/profile") {
            val userId = call.parameters[QueryParameters.QUERY_PARAM_USER_ID]
            if (userId == null || userId.isBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val callResponse = profileService.getUsersProfile(userId = userId)

            if (callResponse == null) {
                call.respond(
                    HttpStatusCode.OK,
                    MainApiResponse<Unit>(
                        false,
                        ERROR_USER_NOT_FOUND
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