package com.endurance.function

import com.endurance.model.*

fun isEmptyAttendee(attendee: Attendee): Boolean = when {
  attendee.attendee_name == "" -> true
  attendee.organization == "" -> true
  else -> false
}

fun isEmptyMinutes(minutes: Minutes): Boolean = when {
  minutes.body_text == "" -> true
  minutes.theme == "" -> true
  minutes.place == "" -> true
  minutes.summary == "" -> true
  else -> false
}

fun isEmptyPicture(picture: Picture): Boolean = when {
  picture.minutes_id == 0 -> false
  else -> true
}

fun isEmptyProject(project: Project): Boolean = when {
  project.project_name == "" -> true
  project.client == "" -> true
  else -> false
}

fun isEmptyTodo(todo: Todo): Boolean = when {
  todo.task_title == "" -> true
  todo.task_body == "" -> true
  todo.start_time_stamp == "" -> true
  todo.end_time_stamp == "" -> true
  else -> false
}

fun isEmptyUser(user: User): Boolean = when {
  user.family_name == "" -> true
  user.last_name == "" -> true
  user.mail_address == "" -> true
  else -> false
}
