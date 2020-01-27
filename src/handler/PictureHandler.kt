package com.endurance.handler

import com.endurance.function.getNowTimeString
import com.endurance.model.IPictureService
import com.endurance.model.Picture
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.readAllParts
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.*
import java.io.File

fun Route.pictureHandler(
  path: String,
  pictureService: IPictureService,
  saveFileOperation: (PartData.FileItem, String) -> Unit,
  deleteFileOperation: (String) -> Unit
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

      val minutesId = multiPartAll
        .filterIsInstance<PartData.FormItem>()
        .filter { part -> part.name == "minutes_id" }
        .elementAtOrNull(0)?.value?.toIntOrNull()

      when (minutesId) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          multiPartAll
            .filterIsInstance<PartData.FileItem>()
            .forEach { part ->
              val fileName = getNowTimeString() + "-" + (part.originalFileName ?: return@forEach)
              kotlin.runCatching {
                pictureService.insert(Picture(0, minutesId, fileName, ""))
              }.onSuccess {
                saveFileOperation(part, fileName)
              }.onFailure {
                throw it
              }
            }

          multiPartAll.forEach { part -> part.dispose() }
          call.respond(HttpStatusCode.OK)
        }
      }
    }

    delete("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          when (val fileName = pictureService.find(id).picture_path) {
            "" -> call.respond(HttpStatusCode.NotFound)
            else -> {
              pictureService.delete(id)
              deleteFileOperation(fileName)
              call.respond(HttpStatusCode.OK)
            }
          }
        }
      }
    }
  }
}
