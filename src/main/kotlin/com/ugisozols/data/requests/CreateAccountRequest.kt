package com.ugisozols.data.requests

data class CreateAccountRequest(
    val email : String,
    val password : String,
    val confirmedPassword : String
)
