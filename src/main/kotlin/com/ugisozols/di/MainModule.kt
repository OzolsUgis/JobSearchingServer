package com.ugisozols.di

import com.ugisozols.data.repository.user_profile.UserProfileRepository
import com.ugisozols.data.repository.user_profile.UserProfileRepositoryImpl
import com.ugisozols.util.Constants
import com.ugisozols.data.repository.user.UserRepository
import com.ugisozols.data.repository.user.UserRepositoryImpl
import com.ugisozols.service.ProfileService
import com.ugisozols.service.UserService
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    single<UserProfileRepository> {
        UserProfileRepositoryImpl(get())
    }
    single {
        UserService(get())
    }
    single {
        ProfileService(get())
    }
}