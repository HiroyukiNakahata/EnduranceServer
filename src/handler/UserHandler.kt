package com.endurance.handler

import com.endurance.function.isNotEmptyUser
import com.endurance.injector.Injector
import com.endurance.model.IUserService
import com.endurance.model.User
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*


fun Route.userHandler(path: String) {
  val userService: IUserService = Injector.getUserService()

  route(path) {
    get {
      val users = userService.findUser()
      call.respond(users)
    }

    get("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> id.also {
          val user = userService.findUser(it)
          call.respond(user)
        }
      }
    }

    post {
      val user = call.receiveOrNull() ?: User(0, "", "", "")
      when (isNotEmptyUser(user)) {
        false -> call.respond(HttpStatusCode.BadRequest)
        true -> {
          userService.insertUser(user)
          call.respond(user)
        }
      }
    }

    put {
      val user = call.receiveOrNull() ?: User(0, "", "", "")
      when (isNotEmptyUser(user)) {
        false -> call.respond(HttpStatusCode.BadRequest)
        true -> {
          userService.updateUser(user)
          call.respond(user)
        }
      }
    }

    delete("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          userService.deleteUser(id)
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }
}
