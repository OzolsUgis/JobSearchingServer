package com.ugisozols.data.repository.sort

import com.ugisozols.data.models.Categories
import com.ugisozols.data.models.User


interface SortRepository {
    suspend fun getUsersByCategory(category : Categories) : List<User>
}