package com.ugisozols.data.repository.user

import com.ugisozols.data.models.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserRepositoryImpl(
    db: CoroutineDatabase
) : UserRepository {
    private val users = db.getCollection<User>()

    override suspend fun createUser(user: User) {
        users.insertOne(user)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq  email)
    }

    override suspend fun checkIfPasswordBelongsToEmail(email: String, passwordToCheck: String): Boolean {
        val realPassword = users.findOne(User::email eq email)?.password ?: false
        return realPassword == passwordToCheck
    }

    override suspend fun getUserById(userId: String): User? {
        return users.findOne(User::id eq userId)
    }
    override suspend fun checkIfUserIdBelongsToAccessTokensUserId(userId: String, callUserId: String): Boolean {
        return userId == callUserId
    }

    override suspend fun editUsersProfile(userId: String, updatedUser: User) : Boolean {
        return users.updateOneById(userId,updatedUser).wasAcknowledged()
    }

    override suspend fun getAllUpdatedUsers(): List<User> {
        return users.find(User::isUpdated eq true).toList()
    }
}