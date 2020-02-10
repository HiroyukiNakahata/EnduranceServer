package com.endurance.service.stub

import com.endurance.model.IUserService
import com.endurance.model.User

class UserServiceStub : IUserService {
  override fun find(): List<User> {
    return listOf(
      User(1, "test", "test", "test@sample.com")
    )
  }

  override fun find(id: Int): User {
    return when (id) {
      1 -> User(1, "test", "test", "test@sample.com")
      else -> User()
    }
  }

  override fun findPasswordByMailAddress(mail_address: String): Pair<String, Int> {
    return Pair(
      "9FD89A274AE758D9D8D98588C367B6C5C77F3C67EF58B26F1AB432EB56EBC0377C80DF2161151A132C69E9039E8DF4B022C28D6C1F0D0FFE66631701993B5582",
      1
    )
  }

  override fun insert(user: User, password: String) {}
  override fun update(user: User) {}
  override fun delete(id: Int) {}
}
