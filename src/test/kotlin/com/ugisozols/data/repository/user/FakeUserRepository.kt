package com.ugisozols.data.repository.user

import com.ugisozols.data.models.User

class FakeUserRepository : UserRepository {

    private val users  = mutableListOf<User>()

    override suspend fun createUser(user: User) {
        users.add(user)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.find { user->
            user.email == email
        }
    }

    override suspend fun checkIfPasswordBelongsToEmail(email: String, passwordToCheck: String): Boolean {
        val user = getUserByEmail(email)
        return user?.password == passwordToCheck
    }

    override suspend fun getUserById(userId: String): User? {
        return users.find { user ->
            user.id == userId
        }
    }

    override suspend fun checkIfUserIdBelongsToAccessTokensUserId(userId: String, callUserId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun editUsersProfile(userId: String, updatedUser: User): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUpdatedUsers(): List<User> {
        TODO("Not yet implemented")
    }
}