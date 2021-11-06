package com.ugisozols.util.security

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

fun passwordHashing(stringToHash : String, saltLength : Int = 32) : String{
    val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
    val hexSalt = Hex.encodeHexString(salt)
    val hash = DigestUtils.sha256Hex("$hexSalt$stringToHash")
    return "$hexSalt:$hash"
}

fun passwordDecoding(password : String, hashWithSalt : String) : Boolean {
    val hashSeparatedFromSalt = hashWithSalt.split(":")
    val salt = hashSeparatedFromSalt[0]
    val hash = hashSeparatedFromSalt[1]
    val passwordHash = DigestUtils.sha256Hex("$salt$password")
    return hash == passwordHash
}