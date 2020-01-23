package com.endurance.injector

import com.endurance.model.*
import com.endurance.service.ProjectService
import com.endurance.service.UserService
import com.endurance.service.MinutesService
import com.endurance.service.MinutesSummaryService
import com.endurance.service.PictureService
import com.endurance.service.AttendeeService
import com.endurance.service.TodoService


object Injector {
  fun getUserService(): IUserService {
    return UserService()
  }

  fun getProjectService(): IProjectService {
    return ProjectService()
  }

  fun getMinutesService(): IMinutesService {
    return MinutesService()
  }

  fun getMinutesSummaryService(): IMinutesSummaryService {
    return MinutesSummaryService()
  }

  fun getPictureService(): IPictureService {
    return PictureService()
  }

  fun getAttendeeService(): IAttendeeService {
    return AttendeeService()
  }

  fun getTodoService(): ITodoService {
    return TodoService()
  }
}
