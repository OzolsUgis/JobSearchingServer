package com.ugisozols

import com.ugisozols.di.mainModule
import io.ktor.application.*
import com.ugisozols.plugins.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.koin.ktor.ext.Koin

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin){
        modules(mainModule)
    }
    configureSecurity()
    configureRouting()
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureHTTP()

}
