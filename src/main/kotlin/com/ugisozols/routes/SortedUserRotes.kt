package com.ugisozols.routes

import com.ugisozols.data.models.Categories
import com.ugisozols.data.models.Keywords
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.data.responses.UserListResponse
import com.ugisozols.service.UserService
import com.ugisozols.util.ApiResponses.ERROR_EMPTY_CATEGORY
import com.ugisozols.util.QueryParameters
import com.ugisozols.util.QueryParameters.QUERY_PARAM_KEYWORDS
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.sortByCategoryAndKeywords(
    userService: UserService
){
    get("api/users/getUserByCategory") {
        val categoryNameQuery = call.parameters[QueryParameters.QUERY_PARAM_CATEGORY]
        val keywordsQuery = call.parameters[QUERY_PARAM_KEYWORDS]
        if(categoryNameQuery == null || categoryNameQuery.isBlank()){
            call.respond(
                HttpStatusCode.BadRequest,
                MainApiResponse<Unit>(
                    false,
                            ERROR_EMPTY_CATEGORY
                )
                )
            return@get
        }
        val usersList = userService.getUsersByCategory(Categories(categoryNameQuery))
        if(keywordsQuery == null || keywordsQuery.isBlank()){
            call.respond(
                HttpStatusCode.OK,
                MainApiResponse(
                    successful = true,
                    data = usersList
                )
            )
        }else{
            val listOfKeywords = keywordsQuery.split("+").toList()
            val sortedByKeyword :MutableList<UserListResponse> = mutableListOf()
            usersList.forEach { user ->
                listOfKeywords.listIterator().forEach { keyword->
                    if(user.keywords.contains(Keywords(keyword))){
                        sortedByKeyword.add(user)
                    }
                }
            }
            call.respond(
                HttpStatusCode.OK,
                MainApiResponse(
                    successful = true,
                    data = sortedByKeyword
                )
            )
        }

    }

}

