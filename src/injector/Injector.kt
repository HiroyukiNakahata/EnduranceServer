package com.endurance.injector

import com.endurance.model.*
import com.endurance.service.*


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
}
