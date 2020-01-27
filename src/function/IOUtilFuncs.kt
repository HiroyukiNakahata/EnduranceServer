package com.endurance.function

import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import java.io.File

fun saveFileItem(part: PartData.FileItem, fileName: String) {
  part.streamProvider().use { its ->
    File("upload/$fileName").outputStream().use {
      its.copyTo(it)
    }
  }
}

fun deleteFileItem(fileName: String) {
  val file = File("upload/$fileName")
  when {
    file.exists() -> file.delete()
  }
}
