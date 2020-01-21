package com.endurance.model

data class User(
  val user_id: Int,
  val family_name: String,
  val last_name: String,
  val mail_address: String
) {
  constructor(): this(0, "", "", "")
}

interface IUserService {
  fun findUser(): List<User>
  fun findUser(id: Int): User
  fun insertUser(user: User)
  fun updateUser(user: User)
  fun deleteUser(id: Int)
}
