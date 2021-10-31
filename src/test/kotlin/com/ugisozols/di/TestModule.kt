package com.ugisozols.di

import com.ugisozols.data.repository.user.FakeUserRepository
import com.ugisozols.data.repository.user.UserRepository
import com.ugisozols.service.UserService
import org.koin.dsl.module

internal val testModule = module {
    single<UserRepository> { FakeUserRepository() }
    single {
        UserService(get())
    }
}