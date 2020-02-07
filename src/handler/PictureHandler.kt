package com.endurance.handler

import com.endurance.function.getNowTimeString
import com.endurance.model.IMinutesService
import com.endurance.model.IPictureService
import com.endurance.model.Picture
import com.endurance.user
import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.readAllParts
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI
import java.io.File

@KtorExperimentalAPI
fun Route.pictureHandler(
  path: String,
  pictureService: IPictureService,
  minutesService: IMinutesService,
  saveFileOperation: (PartData.FileItem, String) -> Unit,
  deleteFileOperation: (String) -> Unit
) {

  route(path) {
    get {
      val pictures = pictureService.findByUser(call.user)
      call.respond(pictures)
    }

    get("/{id}") {
      val pid = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("bad id")

      val picture = pictureService.findByUser(call.user, pid)
      when (picture.picture_id) {
        0 -> call.respond(HttpStatusCode.NotFound)
        else -> call.respond(picture)
      }
    }

    get("/file/{name}") {
      val fileName = call.parameters["name"] ?: throw BadRequestException("bad file name")

      when (pictureService.findUserIdByPicturePath(fileName)) {
        call.user -> {
          val file = File("upload/$fileName")
          when {
            file.exists() -> call.respondFile(file)
            else -> call.respond(HttpStatusCode.NotFound)
          }
        }

        0 -> call.respond(HttpStatusCode.NotFound)
        else -> call.respond(HttpStatusCode.Unauthorized)
      }
    }

    post {
      val multiPartAll = call.receiveMultipart().readAllParts()

      val minutesId = multiPartAll
        .filterIsInstance<PartData.FormItem>()
        .filter { part -> part.name == "minutes_id" }
        .elementAtOrNull(0)?.value?.toIntOrNull() ?: throw BadRequestException("bad id")

      when (minutesService.find(minutesId).user_id) {
        call.user -> {
          multiPartAll
            .filterIsInstance<PartData.FileItem>()
            .forEach { part ->
              val fileName = getNowTimeString() + "-" + (part.originalFileName ?: throw BadRequestException("bad file name"))
              pictureService.insert(Picture(0, minutesId, fileName, ""))
              saveFileOperation(part, fileName)
            }

          multiPartAll.forEach { part -> part.dispose() }
          call.respond(HttpStatusCode.Created)
        }

        0 -> call.respond(HttpStatusCode.NotFound)
        else -> call.respond(HttpStatusCode.Unauthorized)
      }
    }

    delete("/{id}") {
      val pid = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("bad id")

      when (val fileName = pictureService.delete(call.user, pid)) {
        "" -> call.respond(HttpStatusCode.NotFound)
        else -> {
          deleteFileOperation(fileName)
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }
}
