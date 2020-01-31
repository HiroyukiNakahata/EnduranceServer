package com.endurance.model

import io.ktor.auth.Principal

data class User(
  val user_id: Int,
  val first_name: String,
  val last_name: String,
  val mail_address: String
) {
  constructor(): this(0, "", "", "")
}

data class UserLogin(
  val mail_address: String,
  val password: String
) {
  constructor(): this("", "")
}

data class UserCreate(
  val user_id: Int,
  val first_name: String,
  val last_name: String,
  val mail_address: String,
  val password: String
) {
  constructor(): this(0, "", "", "", "")
}

data class IdPrincipal(val id: Int): Principal

interface IUserService {
  fun find(): List<User>
  fun find(id: Int): User
  fun findByMailAddress(mail_address: String): Pair<String, Int>
  fun insert(user: User, password: String)
  fun update(user: User)
  fun delete(id: Int)
}
