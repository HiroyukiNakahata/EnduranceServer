package com.endurance.handler

import com.endurance.function.isEmptyTodo
import com.endurance.model.ITodoService
import com.endurance.model.Todo
import com.endurance.user
import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun Route.todoHandler(
  path: String,
  todoService: ITodoService
) {

  route(path) {
    get {
      val pid = call.request.queryParameters["project"]?.toIntOrNull()
      val mid = call.request.queryParameters["minutes"]?.toIntOrNull()
      val status = call.request.queryParameters["status"]?.toBoolean()
      val limit = call.request.queryParameters["limit"]?.toIntOrNull()
      val offset = call.request.queryParameters["offset"]?.toIntOrNull()

      val todo = todoService.findByUserAndQuery(call.user, pid, mid, status, limit, offset)
      call.respond(todo)
    }

    get("/{id}") {
      val tid = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("bad id")

      val todo = todoService.find(tid)
      when (todo.user_id) {
        call.user -> call.respond(todo)
        0 -> call.respond(HttpStatusCode.NotFound)
        else -> call.respond(HttpStatusCode.Unauthorized)
      }
    }

    get("/count") {
      val pid = call.request.queryParameters["project"]?.toIntOrNull()
      val mid = call.request.queryParameters["minutes"]?.toIntOrNull()
      val status = call.request.queryParameters["status"]?.toBoolean()

      val count = todoService.count(call.user, pid, mid, status)
      call.respond(count)
    }

    post {
      val todo = call.receiveOrNull() ?: Todo()
      when {
        isEmptyTodo(todo) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          todoService.insert(todo)
          call.respond(todo)
        }
      }
    }

    // TODO: マルチインサート実装

    put {
      val todo = call.receiveOrNull() ?: Todo()
      when {
        isEmptyTodo(todo) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          todoService.update(todo)
          call.respond(todo)
        }
      }
    }

    delete("/{id}") {
      val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("bad id")

      todoService.delete(id)
      call.respond(HttpStatusCode.OK)
    }
  }
}
