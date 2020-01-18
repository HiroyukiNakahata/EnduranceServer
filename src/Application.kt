package com.endurance

import com.endurance.handler.rootHandler
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*

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
    exception<Throwable> {
      call.respond(HttpStatusCode.InternalServerError)
    }
  }

  routing {
    rootHandler("/")
  }
}
