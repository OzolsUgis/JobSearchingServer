package com.ugisozols.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import io.ktor.server.testing.*
import java.util.*

object JWTConfig {
    private val configEnvironment = createTestEnvironment() {
        config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
    }
    val issuer = configEnvironment.config.property("jwt.issuer").getString()
    val audience = configEnvironment.config.property("jwt.audience").getString()
    val secret = configEnvironment.config.property("jwt.secret").getString()

    private val algorithm = Algorithm.HMAC256(secret)

    // 1 YEAR
    private const val expiresIn = 1000L * 60L * 60L * 365L

    fun createToken(userEmail : String) : String = JWT.create()
        .withIssuer(issuer)
        .withClaim("email", userEmail)
        .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
        .sign(algorithm)
}