package com.endurance.handler

import com.endurance.function.getNowTimeString
import com.endurance.model.IPictureService
import com.endurance.model.Picture
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.readAllParts
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.*
import java.io.File

fun Route.pictureHandler(
  path: String,
  pictureService: IPictureService
) {

  route(path) {
    get {
      val pictures = pictureService.find()
      call.respond(pictures)
    }

    get("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          val picture = pictureService.find(id)
          when (picture.picture_id) {
            0 -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(picture)
          }
        }
      }
    }

    get("/file/{name}") {
      val fileName = call.parameters["name"]
      val file = File("upload/$fileName")
      when {
        file.exists() -> call.respondFile(file)
        else -> call.respond(HttpStatusCode.NotFound)
      }
    }

    post {
      val multiPartAll = call.receiveMultipart().readAllParts()

      val id = multiPartAll
        .filterIsInstance<PartData.FormItem>()
        .filter { part -> part.name == "id" }
        .elementAtOrNull(0)?.value?.toIntOrNull() ?: 0

      multiPartAll
        .filterIsInstance<PartData.FileItem>()
        .forEach { part ->
          val time = getNowTimeString()
          val fileName = time + "-" + (part.originalFileName ?: "upload.jpg")
          val file = File("upload/$fileName")
          part.streamProvider().use { its ->
            file.outputStream().use {
              its.copyTo(it)
            }
          }

          pictureService.insert(Picture(0, id, fileName, ""))
        }

      multiPartAll.forEach { part -> part.dispose() }
      call.respond(HttpStatusCode.OK)
    }

    put {
      // TODO: ファイル更新
    }

    delete("/{id}") {
      // TODO: ファイル削除
    }
  }
}
