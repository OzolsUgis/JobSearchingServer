package com.ugisozols.routes

import com.google.gson.Gson
import com.ugisozols.data.models.User
import com.ugisozols.data.requests.ProfileUpdateRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.service.UserService
import com.ugisozols.util.Constants
import com.ugisozols.util.ApiResponses.ERROR_ACCESS_DENIED
import com.ugisozols.util.Constants.BASE_URL
import com.ugisozols.util.Constants.PROFILE_PICTURE_PATH
import com.ugisozols.util.QueryParameters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import sun.applet.Main
import java.io.File
import java.util.*

fun Route.updateUser(
    userService: UserService
){
    val gson : Gson by inject()

    authenticate {
        put("api/user/profile/update") {
            val multipartRequest = call.receiveMultipart()
            var updateUserRequest: ProfileUpdateRequest? = null
            var profilePictureFileName: String? = null

            multipartRequest.forEachPart { partData ->
                when(partData){
                    is PartData.FormItem ->{
                        if(partData.name == "profile_update"){
                            updateUserRequest = gson.fromJson(
                                partData.value,
                                ProfileUpdateRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem ->{
                        if(partData.name == "picture_update"){
                            val fileBytes = partData.streamProvider().readBytes()
                            val fileExt = partData.originalFileName?.takeLastWhile {it != '.'}
                            profilePictureFileName = UUID.randomUUID().toString() +"."+ fileExt
                            val folder = File(PROFILE_PICTURE_PATH)
                            folder.mkdir()
                            File("$PROFILE_PICTURE_PATH/$profilePictureFileName").writeBytes(fileBytes)
                        }
                    }
                    is PartData.BinaryItem -> Unit
                }
            }

            val profilePictureUrl = "${BASE_URL}profile_picture/$profilePictureFileName"

            updateUserRequest?.let { request ->
                val updateAcknowledged = userService.updateUser(
                    userId = call.userId?: "",
                    userPicture = if(profilePictureFileName == null){
                       null
                    }else{
                         profilePictureUrl
                    },
                    updateProfileRequest = request
                )
                if(updateAcknowledged){
                    call.respond(
                        HttpStatusCode.OK,
                        MainApiResponse<Unit>(
                            successful = true
                        )
                    )
                }else{
                    File("${PROFILE_PICTURE_PATH}/$profilePictureFileName").delete()
                    call.respond(
                        HttpStatusCode.InternalServerError
                    )
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
        }
    }
}