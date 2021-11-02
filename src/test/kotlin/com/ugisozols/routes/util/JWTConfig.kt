package com.ugisozols.routes.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import com.ugisozols.data.requests.AccountRequest
import com.ugisozols.data.requests.CreateAccountRequest
import io.ktor.config.*
import io.ktor.server.testing.*
import java.util.*

object JWTConfig {
     private val configEnvironment = createTestEnvironment() {
        config = HoconApplicationConfig(ConfigFactory.load("application.conf"))

    }
    val issuer = configEnvironment.config.property("jwt.issuer").getString()
    val secret = configEnvironment.config.property("jwt.secret").getString()
    val audience = configEnvironment.config.property("jwt.audience").getString()
    private val algorithm = Algorithm.HMAC512(secret)
    private val expiresIn = 1000L * 60L * 60L * 24L * 365L

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun createToken(user : AccountRequest) : String = JWT.create()
        .withIssuer(issuer)
        .withClaim("email", user.email)
        .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
        .sign(algorithm)


}