package com.endurance.handler

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.*


fun Route.rootHandler(path: String) {
  route(path) {
    get {
      call.respondText("OK", contentType = ContentType.Text.Plain)
    }
  }
}
