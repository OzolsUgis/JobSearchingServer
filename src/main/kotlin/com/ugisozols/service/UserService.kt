package com.ugisozols.service

import com.ugisozols.data.models.Categories
import com.ugisozols.data.models.CurrentJobState
import com.ugisozols.data.models.Education
import com.ugisozols.data.models.User
import com.ugisozols.data.repository.user.UserRepository
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.data.requests.ProfileUpdateRequest
import com.ugisozols.data.responses.ProfileResponse
import com.ugisozols.data.responses.PublicAccountResponse
import com.ugisozols.data.responses.UserListResponse
import com.ugisozols.util.Constants
import com.ugisozols.util.Constants.DEFAULT_PROFILE_PICTURE
import com.ugisozols.util.ValidationState
import com.ugisozols.util.security.passwordDecoding
import com.ugisozols.util.security.passwordHashing


class UserService(
    private val userRepository: UserRepository
) {

    suspend fun checkForPassword(email: String,passwordToCheck : String) : Boolean{
        val actualPassword = getUserByEmail(email)?.password ?: return false
        return passwordDecoding(passwordToCheck,actualPassword)
    }

    suspend fun getUserByEmail(email: String): User?{
        return userRepository.getUserByEmail(email)
    }

    suspend fun userWithThatEmailAlreadyExists(email : String) : Boolean{
        return userRepository.getUserByEmail(email) != null
    }

    suspend fun createUser(request : CreateAccountRequest){
        userRepository.createUser(
            User(
                email = request.email,
                password = passwordHashing(request.password),
                name = "",
                lastName = "",
                education = Education(""),
                profession = "",
                experience = null,
                profileImageUrl =DEFAULT_PROFILE_PICTURE,
                bio = "",
                instagramUrl = "",
                linkedInUrl = "",
                githubUrl = "",
                skills = listOf(),
                currentJobState = CurrentJobState(""),
                profileUpdateDate = null,
                keywords = listOf(),
                category = Categories(""),
                isUpdated = false
            )
        )
    }

    fun validateCreateUser(request: CreateAccountRequest): ValidationState{
        if (request.email.isBlank() || request.password.isBlank() || request.confirmedPassword.isBlank()){
            return ValidationState.ErrorFieldEmpty
        }
        if (!request.email.contains("@") || !request.email.contains(".")){
            return ValidationState.ErrorEmailIsNotContainingChars
        }
        if(request.password.length < Constants.MIN_PASSWORD_LENGTH){
            return ValidationState.ErrorPasswordToShort
        }
        if(request.password != request.confirmedPassword){
            return ValidationState.ErrorPasswordsAreNotEqual
        }
        return ValidationState.Success

    }

    suspend fun checkIfUsersIdIsEqualToProfileId(userId : String, callUserId: String) : Boolean{
        return userRepository.checkIfUserIdBelongsToAccessTokensUserId(userId,callUserId)
    }

    suspend fun getUsersPublicProfile(userId: String) : PublicAccountResponse?{
        val user = userRepository.getUserById(userId) ?: return null
        return PublicAccountResponse(
            name = user.name,
            lastName = user.lastName,
            profession = user.profession ?: "",
            profileImageUrl = user.profileImageUrl,
            instagramUrl = user.instagramUrl,
            linkedInUrl = user.linkedInUrl,
            githubUrl = user.githubUrl,
            bio = user.bio,
            experience = user.experience,
            education = user.education,
            skills = user.skills,
            currentJobState = user.currentJobState,
            profileUpdateDate = user.profileUpdateDate
        )
    }

    suspend fun getUsersProfile(userId : String) : ProfileResponse? {
        val user = userRepository.getUserById(userId = userId) ?: return null
        return ProfileResponse(
            id = user.id,
            name = user.name,
            lastName = user.lastName,
            education = user.education,
            profession = user.profession.orEmpty(),
            experience = user.experience,
            profileImageUrl = user.profileImageUrl,
            bio = user.bio,
            instagramUrl = user.instagramUrl,
            linkedInUrl = user.linkedInUrl,
            githubUrl = user.githubUrl,
            skills = user.skills,
            currentJobState = user.currentJobState,
            profileUpdateDate = user.profileUpdateDate,
            keywords = user.keywords,
            category = user.category
        )
    }

    suspend fun updateUser(
        userId: String,
        userPicture : String?,
        updateProfileRequest: ProfileUpdateRequest
    ): Boolean {
        return userRepository.editUsersProfile(userId, userPicture, updateProfileRequest)
    }

    suspend fun getAllUsers() : List<UserListResponse>{
        return userRepository.getAllUpdatedUsers().map { user ->
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

    suspend fun deleteUser(userId: String) : Boolean{
        return userRepository.deleteUser(userId)
    }

    suspend fun getUsersByCategory(categories: Categories): List<UserListResponse>{
        return userRepository.getUsersByCategory(categories).map { user ->
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