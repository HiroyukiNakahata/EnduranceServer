package com.endurance.handler

import com.endurance.function.isEmptyTodo
import com.endurance.injector.Injector
import com.endurance.model.Todo
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*


fun Route.todoHandler(path: String) {
  val todoService = Injector.getTodoService()

  route(path) {
    get {
      val todo = todoService.find()
      call.respond(todo)
    }

    get("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          val todo = todoService.find(id)
          when (todo.todo_id) {
            0 -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(todo)
          }
        }
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
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          todoService.delete(id)
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }
}
