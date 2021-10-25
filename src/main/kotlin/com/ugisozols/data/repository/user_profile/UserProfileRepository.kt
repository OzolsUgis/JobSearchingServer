package com.ugisozols.data.repository.user_profile

import com.ugisozols.data.models.User

interface UserProfileRepository {
    suspend fun getUsersProfile(userId : String) : User?
}