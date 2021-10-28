package com.ugisozols.data.repository.sort

import com.ugisozols.data.models.Categories
import com.ugisozols.data.models.User
import com.ugisozols.data.responses.UserListResponse
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class SortRepositoryImpl(
    db : CoroutineDatabase
) : SortRepository {

    private val users = db.getCollection<User>()

    override suspend fun getUsersByCategory(category: Categories): List<User> {
        return users.find(User::category eq category).toList()
    }
}