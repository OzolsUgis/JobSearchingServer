package com.ugisozols.routes

import com.ugisozols.data.models.Categories
import com.ugisozols.data.models.Keywords
import com.ugisozols.data.models.User
import com.ugisozols.data.requests.SortByCategoryRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.data.responses.UserListResponse
import com.ugisozols.service.SortService
import com.ugisozols.util.Constants.ERROR_EMPTY_CATEGORY
import com.ugisozols.util.QueryParameters
import com.ugisozols.util.QueryParameters.QUERY_PARAM_KEYWORDS
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.sort
import sun.applet.Main

fun Route.sortByCategoryAndKeywords(
    sortService: SortService
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
        val usersList = sortService.getUsersByCategory(Categories(categoryNameQuery))
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

