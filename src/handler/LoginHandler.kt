package com.endurance.handler

import com.endurance.authentication.AuthenticationException
import com.endurance.authentication.HashUtil
import com.endurance.authentication.JwtAuth
import com.endurance.model.IUserService
import com.endurance.model.UserLogin
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.loginHandler(
  path: String,
  userService: IUserService
) {

  route(path) {
    post {
      val userLogin = call.receiveOrNull() ?: UserLogin()
      val userHash = HashUtil.sha512(userLogin.password)
      val fromDb = userService.findPasswordByMailAddress(userLogin.mail_address)

      when (fromDb.second) {
        0 -> throw AuthenticationException()
      }

      when (userHash) {
        fromDb.first -> {
          val token = JwtAuth.createToken(fromDb.second, System.currentTimeMillis())
          call.respond(mapOf("token" to token, "id" to fromDb.second))
        }
        else -> call.respond(HttpStatusCode.Unauthorized)
      }
    }
  }
}
