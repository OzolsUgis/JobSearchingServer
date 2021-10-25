package com.ugisozols.service

import com.ugisozols.data.repository.user_profile.UserProfileRepository
import com.ugisozols.data.responses.ProfileResponse

class ProfileService(
    private val userProfileRepository: UserProfileRepository
) {

    suspend fun getUsersProfile(userId : String) : ProfileResponse? {
        val user = userProfileRepository.getUsersProfile(userId = userId) ?: return null
        return ProfileResponse(
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
            category = user.category,
            isOwningProfile = userId == user.id
        )
    }

}