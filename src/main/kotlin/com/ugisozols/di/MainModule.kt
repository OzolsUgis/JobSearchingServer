package com.ugisozols.di


import com.google.gson.Gson
import com.ugisozols.data.repository.sort.SortRepository
import com.ugisozols.data.repository.sort.SortRepositoryImpl
import com.ugisozols.util.Constants
import com.ugisozols.data.repository.user.UserRepository
import com.ugisozols.data.repository.user.UserRepositoryImpl
import com.ugisozols.service.SortService
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
    single<SortRepository> {
        SortRepositoryImpl(get())
    }
    single {
        UserService(get())
    }
    single {
        SortService(get())
    }

    single{Gson()}
}