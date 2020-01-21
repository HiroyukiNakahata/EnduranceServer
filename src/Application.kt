package com.endurance

import com.endurance.handler.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.routing
import org.slf4j.event.Level
import java.sql.SQLException


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
  install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/") }
  }

  install(ContentNegotiation) {
    gson {
      setPrettyPrinting()
    }
  }

  install(StatusPages) {
    exception<SQLException> { cause ->
      println(cause.message)
      call.respond(HttpStatusCode.InternalServerError)
    }
  }

  routing {
    rootHandler("/")
    userHandler("/api/user")
    projectHandler("/api/project")
    minutesHandler("/api/minutes")
    pictureHandler("/api/picture")
    attendeeHandler("/api/attendee")
    todoHandler("/api/todo")
  }
}
