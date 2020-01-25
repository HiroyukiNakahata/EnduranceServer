package com.endurance.model

data class User(
  val user_id: Int,
  val first_name: String,
  val last_name: String,
  val mail_address: String
) {
  constructor(): this(0, "", "", "")
}

interface IUserService {
  fun find(): List<User>
  fun find(id: Int): User
  fun insert(user: User)
  fun update(user: User)
  fun delete(id: Int)
}
