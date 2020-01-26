package com.endurance.handler

import com.endurance.function.isEmptyUser
import com.endurance.model.IUserService
import com.endurance.model.User
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.userHandler(
  path: String,
  userService: IUserService
) {

  route(path) {
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
      val user = call.receiveOrNull() ?: User()
      when {
        isEmptyUser(user) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          userService.insert(user)
          call.respond(user)
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
