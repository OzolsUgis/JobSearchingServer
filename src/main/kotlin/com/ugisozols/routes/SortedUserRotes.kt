package com.ugisozols.routes

import com.ugisozols.data.models.Categories
import com.ugisozols.data.requests.SortByCategoryRequest
import com.ugisozols.data.responses.MainApiResponse
import com.ugisozols.service.SortService
import com.ugisozols.util.QueryParameters
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.sort

fun Route.sortByCategory(
    sortService: SortService
){
    get("api/users/getUsersByCategory") {
        val categoryName = call.parameters[QueryParameters.QUERY_PARAM_CATEGORY]
        if(categoryName == null || categoryName.isBlank()){
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val usersList = sortService.getUsersByCategory(Categories(categoryName))
        call.respond(
            HttpStatusCode.OK,
            MainApiResponse(
                successful = true,
                data = usersList
            )
        )
    }
}