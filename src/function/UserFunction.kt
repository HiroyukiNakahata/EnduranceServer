package com.endurance.function

import com.endurance.model.User

fun isNotEmptyUser(user: User): Boolean = when {
  user.family_name == "" -> false
  user.last_name == "" -> false
  user.mail_address == "" -> false
  else -> true
}
