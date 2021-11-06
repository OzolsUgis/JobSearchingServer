package com.ugisozols.data.repository.user

import com.ugisozols.data.models.User
import com.ugisozols.data.requests.ProfileUpdateRequest
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import javax.jws.soap.SOAPBinding

class UserRepositoryImpl(
    db: CoroutineDatabase
) : UserRepository {
    private val users = db.getCollection<User>()

    override suspend fun createUser(user: User) {
        users.insertOne(user)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq  email)
    }

    override suspend fun checkIfPasswordBelongsToEmail(email: String, passwordToCheck: String): Boolean {
        val realPassword = users.findOne(User::email eq email)?.password ?: false
        return realPassword == passwordToCheck
    }

    override suspend fun getUserById(userId: String): User? {
        return users.findOne(User::id eq userId)
    }
    override suspend fun checkIfUserIdBelongsToAccessTokensUserId(userId: String, callUserId: String): Boolean {
        return userId == callUserId
    }

    override suspend fun editUsersProfile(userId: String, userPicture : String?, updatedUser: ProfileUpdateRequest) : Boolean {
        val user = getUserById(userId) ?: return false
        return users.updateOneById(
            id = userId,
            update = User(
                email = user.email,
                password = user.password,
                name = updatedUser.name,
                lastName = updatedUser.lastName,
                education = updatedUser.education,
                profession = updatedUser.profession,
                experience = updatedUser.experience,
                profileImageUrl = userPicture ?: user.profileImageUrl,
                bio = updatedUser.bio,
                instagramUrl = updatedUser.instagramUrl,
                linkedInUrl = updatedUser.linkedInUrl,
                githubUrl = updatedUser.githubUrl,
                skills = updatedUser.skills,
                currentJobState = updatedUser.currentJobState,
                profileUpdateDate = System.currentTimeMillis(),
                keywords = updatedUser.keywords,
                category = updatedUser.category,
                isUpdated = true,
                id = user.id
            )
        ).wasAcknowledged()
    }

    override suspend fun getAllUpdatedUsers(): List<User> {
        return users.find(User::isUpdated eq true).toList()
    }

    override suspend fun deleteUser(userId: String): Boolean {
        return users.deleteOneById(userId).wasAcknowledged()
    }
}
