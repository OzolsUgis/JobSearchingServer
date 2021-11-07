package com.ugisozols.di

import com.ugisozols.data.repository.FakeUserRepository
import com.ugisozols.data.repository.user.UserRepository
import com.ugisozols.service.UserService
import org.koin.core.scope.get
import org.koin.dsl.module


internal val fakeModule = module {
    single<UserRepository> {
        FakeUserRepository()
    }

    single { UserService(get()) }
}