package com.ugisozols.util

import com.ugisozols.data.models.Categories
import com.ugisozols.data.models.Keywords
import com.ugisozols.data.requests.CreateAccountRequest
import com.ugisozols.data.requests.ProfileUpdateRequest
import com.ugisozols.service.UserService
import kotlinx.coroutines.runBlocking

fun createMockUser(userService: UserService) = runBlocking{
    val user = CreateAccountRequest(
        email = "Test@test.com",
        password = "123456789",
        confirmedPassword = "123456789"
    )
    userService.createUser(user)
}

fun createSecondUser(userService: UserService) = runBlocking{
        val user = CreateAccountRequest(
            email = "Username@test.com",
            password = "123456789",
            confirmedPassword = "123456789"
        )
        userService.createUser(user)
    }

fun getUsersId (userService: UserService)= runBlocking {
    userService.getUserByEmail("Test@test.com")
}

fun createMockUpdatedUser(userService: UserService) = runBlocking {
    createMockUser(userService)
    val userId = userService.getUserByEmail("Test@test.com")?.id ?:" "
    val user = ProfileUpdateRequest(
        email = "Test@test.com",
        password = "123456789",
        name = "John",
        lastName = "Doe",
        profileImageUrl = "",
        education = null,
        profession = null,
        experience = null,
        bio = "Test",
        instagramUrl = null,
        linkedInUrl = null,
        githubUrl = null,
        skills = listOf(),
        currentJobState = null,
        profileUpdateDate = System.currentTimeMillis(),
        keywords = listOf(Keywords("Android"), Keywords("Kotlin")),
        category = Categories("test"),
        isUpdated = true
    )
    userService.updateUser(userId,"",user)
}




