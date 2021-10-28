package com.ugisozols.data.responses


import com.ugisozols.data.models.*

data class ProfileResponse(
    val id : String,
    val name : String,
    val lastName : String,
    val profession : String,
    val profileImageUrl : String?,
    val instagramUrl : String?,
    val linkedInUrl : String?,
    val githubUrl : String?,
    val bio : String?,
    val experience : Int?,
    val education : Education?,
    val skills : List<Skills> = listOf(),
    val currentJobState : CurrentJobState? = null,
    val profileUpdateDate : Long?,
    val keywords : List<Keywords> = listOf(),
    val category : Categories?
)
