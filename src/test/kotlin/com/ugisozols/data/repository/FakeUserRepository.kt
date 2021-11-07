package com.ugisozols.data.repository

import com.ugisozols.data.models.Categories
import com.ugisozols.data.models.User
import com.ugisozols.data.repository.user.UserRepository
import com.ugisozols.data.requests.ProfileUpdateRequest
import com.ugisozols.util.security.passwordDecoding


internal class FakeUserRepository : UserRepository {

    private val users = mutableListOf<User>()

    override suspend fun createUser(user: User) {
        users.add(user)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    override suspend fun checkIfPasswordBelongsToEmail(email: String, passwordToCheck: String): Boolean {
        val userPassword = users.find { it.email == email }?.password ?: return false
        return passwordDecoding(passwordToCheck, userPassword)
    }

    override suspend fun getUserById(userId: String): User? {
       return users.find { it.id == userId }
    }

    override suspend fun checkIfUserIdBelongsToAccessTokensUserId(userId: String, callUserId: String): Boolean {
        val userId = users.find { it.id == userId }?.id ?: return false
        return userId == callUserId
    }

    override suspend fun editUsersProfile(
        userId: String,
        userPicture: String?,
        updatedUser: ProfileUpdateRequest
    ): Boolean {
        val user = users.find{it.id == userId} ?: return false
        val index = users.indexOf(user)
        users[index] = User(
            email = user.email,
            password = user.password,
            name = updatedUser.name,
            lastName = updatedUser.lastName,
            education = updatedUser.education,
            profession = updatedUser.profession,
            experience = updatedUser.experience,
            profileImageUrl = userPicture ?: user.profileImageUrl,
            bio = updatedUser.bio,
            instagramUrl = updatedUser.instagramUrl,
            linkedInUrl = updatedUser.linkedInUrl,
            githubUrl = updatedUser.githubUrl,
            skills = updatedUser.skills,
            currentJobState = updatedUser.currentJobState,
            profileUpdateDate = System.currentTimeMillis(),
            keywords = updatedUser.keywords,
            category = updatedUser.category,
            isUpdated = true,
            id = user.id
        )
        return true
    }

    override suspend fun getAllUpdatedUsers(): List<User> {
        val listOfUpdatedUsers = mutableListOf<User>()
        users.forEach {
            if(it.isUpdated){
                listOfUpdatedUsers.add(it)
            }
        }
        return listOfUpdatedUsers
    }

    override suspend fun deleteUser(userId: String): Boolean {
        val user = users.find { it.id == userId } ?: return false
        return users.remove(user)
    }

    override suspend fun getUsersByCategory(category: Categories): List<User> {
        val listOfUsers = mutableListOf<User>()
        users.forEach {
            if(it.category == category){
                listOfUsers.add(it)
            }
        }
        return listOfUsers
    }
}