package com.endurance

import com.endurance.authentication.AuthenticationException
import com.endurance.authentication.JwtAuth
import com.endurance.handler.*
import com.endurance.injector.Injector
import com.endurance.model.IdPrincipal
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.sql.SQLException

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
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

    exception<AuthenticationException> {
      call.respond(HttpStatusCode.Unauthorized)
    }

    exception<BadRequestException> { cause ->
      call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
    }

    exception<NotFoundException> {cause ->
      call.respond(HttpStatusCode.NotFound, cause.message ?: "")
    }
  }

  routing {
    rootHandler("/")

    loginHandler(
      "/login",
      Injector.getUserService()
    )

    authenticate {
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
        Injector.getMinutesService(),
        Injector.getSaveFileOperation(),
        Injector.getDeleteFileOperation()
      )

      attendeeHandler(
        "/api/attendee",
        Injector.getAttendeeService(),
        Injector.getMinutesService()
      )

      todoHandler(
        "/api/todo",
        Injector.getTodoService()
      )
    }
  }
}

val ApplicationCall.user get() = authentication.principal<IdPrincipal>()?.id ?: 0
