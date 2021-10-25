package com.ugisozols.data.repository.user_profile

import com.ugisozols.data.models.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserProfileRepositoryImpl(
    db : CoroutineDatabase
) : UserProfileRepository {

    private val user = db.getCollection<User>()

    override suspend fun getUsersProfile(userId: String): User? {
        return user.findOne(User::id eq userId)
    }
}