package com.endurance.model

data class Todo(
  val todo_id: Int,
  val minutes_id: Int,
  val project_id: Int,
  val user_id: Int,
  val task_title: String,
  val task_body: String,
  val start_time_stamp: String,
  val end_time_stamp: String,
  val status: Boolean
) {
  constructor() : this(0, 0, 0, 0, "", "", "", "", false)
}

interface ITodoService {
  fun find(): List<Todo>
  fun find(id: Int): Todo
  fun insert(todo: Todo)
  fun update(todo: Todo)
  fun delete(id: Int)
}
