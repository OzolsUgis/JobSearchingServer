package com.ugisozols.data.requests

import com.ugisozols.data.models.*

data class ProfileUpdateRequest(
    val email : String,
    val password : String,
    val name : String,
    val lastName : String,
    val education : Education? = null,
    val profession : String?,
    val experience : Int?,
    val profileImageUrl : String,
    val bio : String?,
    val instagramUrl : String?,
    val linkedInUrl : String?,
    val githubUrl : String?,
    val skills : List<Skills> = listOf(),
    val currentJobState : CurrentJobState? = null,
    val profileUpdateDate : Long?,
    val keywords : List<Keywords> = listOf(),
    val category : Categories?,
    val isUpdated : Boolean
)