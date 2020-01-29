package com.endurance

import com.endurance.authentication.HashUtil
import com.endurance.authentication.JwtAuth
import com.endurance.handler.*
import com.endurance.injector.Injector
import com.endurance.model.IdPrincipal
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.sql.SQLException

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
  Injector.testing = testing

  install(Authentication) {
    jwt {
      verifier(JwtAuth.verifier)
      realm = "endurance"
      validate { jwtCredential ->
        jwtCredential.payload.getClaim("id").asInt()?.let { IdPrincipal(it) }
      }
    }
  }

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
    val loggerSQL = LoggerFactory.getLogger("SQLException")
    exception<SQLException> { cause ->
      loggerSQL.error(cause.message)
      loggerSQL.error(cause.sqlState)
      call.respond(HttpStatusCode.InternalServerError)
    }
  }

  routing {
    rootHandler("/")
    userHandler(
      "/api/user",
      Injector.getUserService()
    )
    projectHandler(
      "/api/project",
      Injector.getProjectService()
    )
    minutesHandler(
      "/api/minutes",
      Injector.getMinutesService(),
      Injector.getMinutesSummaryService(),
      Injector.getMinutesAllService()
    )
    pictureHandler(
      "/api/picture",
      Injector.getPictureService(),
      Injector.getSaveFileOperation(),
      Injector.getDeleteFileOperation()
    )
    attendeeHandler(
      "/api/attendee",
      Injector.getAttendeeService()
    )
    todoHandler(
      "/api/todo",
      Injector.getTodoService()
    )

    post("/login") {
      val token = JwtAuth.createToken(1, System.currentTimeMillis())
      call.respond(token)
    }

    authenticate {
      get("/secure") {
        val id = call.authentication.principal<IdPrincipal>()?.id ?: 0
        val hashTest = HashUtil.sha512("hashTest")
        call.respond(hashTest)
      }
    }
  }
}
