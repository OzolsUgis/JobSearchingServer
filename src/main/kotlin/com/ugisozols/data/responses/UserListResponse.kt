package com.ugisozols.data.responses

import com.ugisozols.data.models.CurrentJobState
import com.ugisozols.data.models.Education

data class UserListResponse(
    val id : String,
    val name : String,
    val lastName : String,
    val profession : String?,
    val experience : Int?,
    val education: Education?,
    val currentJobState : CurrentJobState?

)