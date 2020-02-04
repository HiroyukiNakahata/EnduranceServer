package com.endurance.handler

import com.endurance.function.isEmptyTodo
import com.endurance.model.ITodoService
import com.endurance.model.Todo
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
      val todo = todoService.find()
      call.respond(todo)
    }

    get("/{id}") {
      val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("bad id")

      val todo = todoService.find(id)
      when (todo.todo_id) {
        0 -> call.respond(HttpStatusCode.NotFound)
        else -> call.respond(todo)
      }
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
