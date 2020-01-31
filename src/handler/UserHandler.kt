package com.endurance.handler

import com.endurance.authentication.AuthenticationException
import com.endurance.authentication.HashUtil
import com.endurance.function.isEmptyUser
import com.endurance.model.IUserService
import com.endurance.model.IdPrincipal
import com.endurance.model.User
import com.endurance.model.UserCreate
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.userHandler(
  path: String,
  userService: IUserService
) {

  route(path) {
    intercept(ApplicationCallPipeline.Call) {
      val uid = call.authentication.principal<IdPrincipal>()?.id ?: 0
      when {
        uid != 1 -> throw AuthenticationException()
      }
    }

    get {
      val users = userService.find()
      call.respond(users)
    }

    get("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          val user = userService.find(id)
          when (user.user_id) {
            0 -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(user)
          }
        }
      }
    }

    post {
      val userCreate = call.receiveOrNull() ?: UserCreate()
      val user = User(
        userCreate.user_id,
        userCreate.first_name,
        userCreate.last_name,
        userCreate.mail_address
      )
      when {
        isEmptyUser(user) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          userService.insert(user, HashUtil.sha512(userCreate.password))
          call.respond(HttpStatusCode.Created, user)
        }
      }
    }

    put {
      val user = call.receiveOrNull() ?: User()
      when {
        isEmptyUser(user) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          userService.update(user)
          call.respond(user)
        }
      }
    }

    delete("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          userService.delete(id)
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }
}
