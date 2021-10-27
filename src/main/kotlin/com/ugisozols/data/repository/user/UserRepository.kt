package com.ugisozols.data.repository.user

import com.ugisozols.data.models.User

interface UserRepository {
    suspend fun createUser(user : User)
    suspend fun getUserByEmail(email : String) : User?
    suspend fun checkIfPasswordBelongsToEmail(email : String, passwordToCheck : String) : Boolean
    suspend fun getUserById(userId : String) : User?
    suspend fun checkIfUserIdBelongsToAccessTokensUserId(userId : String , callUserId : String) : Boolean
    suspend fun editUsersProfile(userId : String, updatedUser: User) : Boolean

}