package com.endurance

import com.endurance.handler.projectHandler
import com.endurance.handler.rootHandler
import com.endurance.handler.userHandler
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.request.path
import io.ktor.routing.routing
import org.slf4j.event.Level


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

//  install(StatusPages) {
//    exception<Throwable> {
//      call.respond(HttpStatusCode.InternalServerError)
//    }
//  }

  routing {
    rootHandler("/")
    userHandler("/api/user")
    projectHandler("/api/project")
  }
}
