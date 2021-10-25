package com.ugisozols.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ugisozols.data.requests.AccountRequest
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.data.responses.AuthResponse
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.service.UserService
import com.ugisozols.util.Constants
import com.ugisozols.util.Constants.ACCOUNT_CREATED
import com.ugisozols.util.Constants.ERROR_EMAIL_ALREADY_EXISTS
import com.ugisozols.util.Constants.ERROR_EMAIL_DOES_NOT_CONTAIN_EMAIL_CHARS
import com.ugisozols.util.Constants.ERROR_FIELDS_EMPTY
import com.ugisozols.util.Constants.ERROR_PASSWORDS_DO_NOT_MATCH
import com.ugisozols.util.Constants.ERROR_PASSWORD_IS_TOO_SHORT
import com.ugisozols.util.Constants.ERROR_PASSWORD_OR_EMAIL_INCORRECT
import com.ugisozols.util.ValidationState
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*


fun Route.createUser(userService: UserService){
    post("api/userAuth/createUser"){
        val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if (userService.userWithThatEmailAlreadyExists(request.email)){
            call.respond(MainApiResponse(
                successful = false,
                message = ERROR_EMAIL_ALREADY_EXISTS
            ))
            return@post
        }
        when(userService.validateCreateUser(request)){
            is ValidationState.ErrorFieldEmpty -> {
                call.respond(MainApiResponse(
                    successful = false,
                    message = ERROR_FIELDS_EMPTY
                ))
                return@post
            }
            is ValidationState.ErrorEmailIsNotContainingChars -> {
                call.respond(MainApiResponse(
                    successful = false,
                    message = ERROR_EMAIL_DOES_NOT_CONTAIN_EMAIL_CHARS
                ))
                return@post
            }
            is ValidationState.ErrorPasswordsAreNotEqual -> {
                call.respond(MainApiResponse(
                    successful = false,
                    message = ERROR_PASSWORDS_DO_NOT_MATCH
                ))
                return@post
            }
            is ValidationState.ErrorPasswordToShort -> {
                call.respond(MainApiResponse(
                    successful = false,
                    message = ERROR_PASSWORD_IS_TOO_SHORT
                ))
                return@post
            }
            is ValidationState.Success -> {
                userService.createUser(request)
                call.respond(MainApiResponse(
                    successful = true,
                    message = ACCOUNT_CREATED
                ))
            }
        }

    }
}

fun Route.loginUser(
    userService: UserService,
    jwtIssuer :String,
    jwtAudience : String,
    jwtSecret : String

){
    post("api/userAuth/loginUser") {
        val request  = call.receiveOrNull<AccountRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if(request.email.isBlank() || request.password.isBlank()){
            call.respond(
                HttpStatusCode.BadRequest,
                MainApiResponse(
                    false,
                    message = ERROR_FIELDS_EMPTY
                )
            )
            return@post
        }
        val user = userService.getUserByEmail(request.email)?: kotlin.run {
            call.respond(
                HttpStatusCode.OK,
                MainApiResponse(
                    false,
                    ERROR_PASSWORD_OR_EMAIL_INCORRECT
                )
            )
            return@post

        }

        val passwordIsCorrect = userService.checkForPassword(
            actualPassword = user.password,
            passwordToCheck = request.password
        )



        if(passwordIsCorrect){
            val expiresIn = 1000L * 60L * 60L * 24L * 365L
            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .withClaim("email", user.email)
                .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    userId = user.id,
                    token = token
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                MainApiResponse(
                    false,
                    ERROR_PASSWORD_OR_EMAIL_INCORRECT
                )
            )
        }
    }
}