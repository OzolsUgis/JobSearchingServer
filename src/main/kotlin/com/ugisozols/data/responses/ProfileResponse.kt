package com.ugisozols.data.responses

import com.sun.org.apache.xpath.internal.operations.Bool
import com.ugisozols.data.models.*

data class ProfileResponse(
    val name : String,
    val lastName : String,
    val education : Education?,
    val profession : String,
    val experience : Int?,
    val profileImageUrl : String?,
    val bio : String?,
    val instagramUrl : String?,
    val linkedInUrl : String?,
    val githubUrl : String?,
    val skills : List<Skills> = listOf(),
    val currentJobState : CurrentJobState? = null,
    val profileUpdateDate : Long?,
    val keywords : List<Keywords> = listOf(),
    val category : Categories?,
    val isOwningProfile : Boolean
)
