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
  fun find(todoId: Int): Todo
  fun findByUserAndQuery(
    userId: Int,
    projectId: Int?,
    minutesId: Int?,
    status: Boolean?,
    limit: Int?,
    offset: Int?
  ): List<Todo>

  fun count(userId: Int, projectId: Int?, minutesId: Int?, status: Boolean?): Int
  fun insert(todo: Todo)
  fun insertMulti(todoList: List<Todo>)
  fun update(todo: Todo)
  fun delete(todoId: Int)
  fun delete(todoId: Int, userId: Int)
}
