package com.ugisozols.data.repository.user

import com.ugisozols.data.models.Categories
import com.ugisozols.data.models.User
import com.ugisozols.data.requests.ProfileUpdateRequest

interface UserRepository {
    suspend fun createUser(user : User)
    suspend fun getUserByEmail(email : String) : User?
    suspend fun checkIfPasswordBelongsToEmail(email : String, passwordToCheck : String) : Boolean
    suspend fun getUserById(userId : String) : User?
    suspend fun checkIfUserIdBelongsToAccessTokensUserId(userId : String , callUserId : String) : Boolean
    suspend fun editUsersProfile(userId : String,userPicture : String?, updatedUser: ProfileUpdateRequest) : Boolean
    suspend fun getAllUpdatedUsers() : List<User>
    suspend fun deleteUser(userId: String) : Boolean
    suspend fun getUsersByCategory(category : Categories) : List<User>

}