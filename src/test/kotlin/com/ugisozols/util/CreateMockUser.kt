package com.ugisozols.util

import com.ugisozols.data.requests.CreateAccountRequest
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
