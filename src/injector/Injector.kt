package com.endurance.injector

import com.endurance.model.IUserService
import com.endurance.service.UserService

object Injector {
  fun getUserService(): IUserService {
    return UserService()
  }
}
