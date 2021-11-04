package com.ugisozols.service

import com.ugisozols.data.models.Categories
import com.ugisozols.data.repository.sort.SortRepository
import com.ugisozols.data.responses.UserListResponse

class SortService(
     private val sortRepository: SortRepository
) {
    suspend fun getUsersByCategory(categories: Categories): List<UserListResponse>{
        return sortRepository.getUsersByCategory(categories).map { user ->
            UserListResponse(
                id = user.id,
                name = user.name,
                lastName = user.lastName,
                profession = user.profession,
                experience = user.experience,
                education = user.education,
                currentJobState = user.currentJobState,
                keywords = user.keywords
            )
        }
    }
}