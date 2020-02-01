package com.endurance.injector

import com.endurance.function.deleteFileItem
import com.endurance.function.saveFileItem
import com.endurance.model.*
import com.endurance.service.*
import io.ktor.http.content.PartData

object Injector {
  var testing: Boolean = false

  fun getUserService(): IUserService = when {
    testing -> UserServiceStub()
    else -> UserService()
  }

  fun getProjectService(): IProjectService = when {
    testing -> ProjectServiceStub()
    else -> ProjectService()
  }

  fun getMinutesService(): IMinutesService = when {
    testing -> MinutesServiceStub()
    else -> MinutesService()
  }

  fun getMinutesSummaryService(): IMinutesSummaryService = when {
    testing -> MinutesSummaryServiceStub()
    else -> MinutesSummaryService()
  }

  fun getMinutesAllService(): IMinutesAllService = when {
    testing -> MinutesAllServiceStub()
    else -> MinutesAllService()
  }

  fun getPictureService(): IPictureService = when {
    testing -> PictureServiceStub()
    else -> PictureService()
  }

  fun getAttendeeService(): IAttendeeService = when {
    testing -> AttendeeServiceStub()
    else -> AttendeeService()
  }

  fun getTodoService(): ITodoService = when {
    testing -> TodoServiceStub()
    else -> TodoService()
  }

  fun getSaveFileOperation(): (PartData.FileItem, String) -> Unit = when {
    testing -> { _: PartData.FileItem, _: String -> }
    else -> ::saveFileItem
  }

  fun getDeleteFileOperation(): (String) -> Unit = when {
    testing -> { _: String -> }
    else -> ::deleteFileItem
  }
}
