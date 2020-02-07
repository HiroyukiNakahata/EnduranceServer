package com.endurance.handler

import com.endurance.function.isEmptyMinutes
import com.endurance.model.IMinutesAllService
import com.endurance.model.IMinutesService
import com.endurance.model.IMinutesSummaryService
import com.endurance.model.Minutes
import com.endurance.user
import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun Route.minutesHandler(
  path: String,
  minutesService: IMinutesService,
  minutesSummaryService: IMinutesSummaryService,
  minutesAllService: IMinutesAllService
) {

  route(path) {
    get {
      val minutes = minutesService.findByUser(call.user)
      call.respond(minutes)
    }

    get("/{id}") {
      val mid = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("bad id")

      val minutes = minutesService.find(mid)
      when (minutes.user_id) {
        call.user -> call.respond(minutes)
        0 -> call.respond(HttpStatusCode.NotFound)
        else -> call.respond(HttpStatusCode.Unauthorized)
      }
    }

    get("/all/{id}") {
      val mid = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("bad id")

      val minutesAll = minutesAllService.find(mid)
      when (minutesAll.user_id) {
        call.user -> call.respond(minutesAll)
        0 -> call.respond(HttpStatusCode.NotFound)
        else -> call.respond(HttpStatusCode.Unauthorized)
      }
    }

    get("/summary") {
      val pid = call.request.queryParameters["project"]?.toIntOrNull()
      val limit = call.request.queryParameters["limit"]?.toIntOrNull()
      val offset = call.request.queryParameters["offset"]?.toIntOrNull()

      val mSummary = minutesSummaryService.findByUserAndQuery(call.user, pid, limit, offset)

      call.respond(mSummary)
    }

    get("/summary/count") {
      val count = when (val pid = call.request.queryParameters["project"]?.toIntOrNull()) {
        null -> minutesSummaryService.count(call.user)
        else -> minutesSummaryService.count(call.user, pid)
      }
      call.respond(count)
    }

    post {
      val minutes = call.receiveOrNull() ?: Minutes()
      when {
        isEmptyMinutes(minutes) -> call.respond(HttpStatusCode.BadRequest)
        minutes.user_id != call.user -> call.respond(HttpStatusCode.Unauthorized)
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
        minutes.user_id != call.user -> call.respond(HttpStatusCode.Unauthorized)
        else -> {
          minutesService.update(minutes)
          call.respond(minutes)
        }
      }
    }

    delete("/{id}") {
      val mid = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("bad id")

      minutesService.delete(call.user, mid)
      call.respond(HttpStatusCode.OK)
    }
  }
}
