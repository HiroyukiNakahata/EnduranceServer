package com.endurance.handler

import com.endurance.model.IMinutesService
import com.endurance.model.IMinutesSummaryService
import com.endurance.model.Minutes
import com.endurance.injector.Injector
import com.endurance.function.isEmptyMinutes
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.minutesHandler(path: String) {
  val minutesService: IMinutesService = Injector.getMinutesService()
  val minutesSummaryService: IMinutesSummaryService = Injector.getMinutesSummaryService()

  route(path) {
    get {
      val minutes = minutesService.find()
      call.respond(minutes)
    }

    get("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          val minutes = minutesService.find(id)
          call.respond(minutes)
        }
      }
    }

    get("/summary") {
      val limit = call.request.queryParameters["limit"]?.toIntOrNull()
      val offset = call.request.queryParameters["offset"]?.toIntOrNull()
      when {
        limit == null || offset == null -> {
          val mSummary = minutesSummaryService.find()
          call.respond(mSummary)
        }
        else -> {
          val mSummary = minutesSummaryService.find(limit, offset)
          call.respond(mSummary)
        }
      }
    }

    get("/summary/user/{id}") {
      val id = call.parameters["id"]?.toIntOrNull()
      val limit = call.request.queryParameters["limit"]?.toIntOrNull()
      val offset = call.request.queryParameters["offset"]?.toIntOrNull()
      when {
        id == null -> call.respond(HttpStatusCode.BadRequest)
        limit == null || offset == null -> {
          val mSummary = minutesSummaryService.findByUser(id)
          call.respond(mSummary)
        }
        else -> {
          val mSummary = minutesSummaryService.findByUser(id, limit, offset)
          call.respond(mSummary)
        }
      }
    }

    get("/summary/project/{id}") {
      val id = call.parameters["id"]?.toIntOrNull()
      val limit = call.request.queryParameters["limit"]?.toIntOrNull()
      val offset = call.request.queryParameters["offset"]?.toIntOrNull()
      when {
        id == null -> call.respond(HttpStatusCode.BadRequest)
        limit == null || offset == null -> {
          val mSummary = minutesSummaryService.findByProject(id)
          call.respond(mSummary)
        }
        else -> {
          val mSummary = minutesSummaryService.findByProject(id, limit, offset)
          call.respond(mSummary)
        }
      }
    }

    post {
      val minutes = call.receiveOrNull() ?: Minutes()
      when {
        isEmptyMinutes(minutes) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          minutesService.insert(minutes)
          call.respond(minutes)
        }
      }
    }

    put {
      val minutes = call.receiveOrNull() ?: Minutes()
      when {
        isEmptyMinutes(minutes) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          minutesService.update(minutes)
          call.respond(minutes)
        }
      }
    }

    delete("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          minutesService.delete(id)
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }
}
