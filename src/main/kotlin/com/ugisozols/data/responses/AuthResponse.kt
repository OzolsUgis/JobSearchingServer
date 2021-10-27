package com.ugisozols.data.responses

data class AuthResponse(
    val userId : String,
    val email : String,
    val token : String
)
