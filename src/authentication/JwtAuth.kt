package com.endurance.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtAuth {
  private const val key = "APlaceShibuyaHigashi"
  private const val issuer = "endurance_api_server"
  private const val expireMillSec = 3_600_000 * 1  // 1時間
  private val encryptAlgorithm = Algorithm.HMAC512(key)

  val verifier: JWTVerifier = JWT.require(encryptAlgorithm).withIssuer(
    issuer
  ).build()

  fun createToken(userId: Int, currentTime: Long): String = JWT.create()
    .withSubject("Authentication")
    .withIssuer(issuer)
    .withClaim("id", userId)
    .withExpiresAt(Date(currentTime + expireMillSec))
    .sign(encryptAlgorithm)
}
