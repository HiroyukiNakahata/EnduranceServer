package com.endurance.function

import com.endurance.model.User

fun checkEmptyUser(user: User): Boolean {
  if (user.family_name == "") {
    return false
  }
  if (user.last_name == "") {
    return false
  }
  if (user.mail_address == "") {
    return false
  }

  return true
}
