package com.ugisozols.service

import com.ugisozols.data.models.User
import com.ugisozols.data.repository.user.UserRepository
import com.ugisozols.data.requests.AccountRequest
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.util.Constants
import com.ugisozols.util.ValidationState

class UserService(
    private val userRepository: UserRepository
) {

     fun checkForPassword(actualPassword : String,passwordToCheck : String) : Boolean{
        return actualPassword == passwordToCheck
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
                password = request.password,
                name = "",
                lastName = "",
                education = null,
                profession = "",
                experience = null,
                profileImageUrl = "",
                bio = "",
                instagramUrl = null,
                linkedInUrl = null,
                githubUrl = null,
                skills = listOf(),
                currentJobState = null,
                profileUpdateDate = null,
                keywords = listOf(),
                category = null
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



}