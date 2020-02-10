package com.endurance.service.stub

import com.endurance.model.ITodoService
import com.endurance.model.Todo

class TodoServiceStub : ITodoService {
  override fun find(): List<Todo> {
    return listOf(
      Todo(
        1, 1, 1, 1, "四次元方程式を解く", "時空間４次元を表現する数式",
        "2020-01-29 00:00:00+09", "2020-01-29 00:00:00+09", false
      ),
      Todo(
        2, 3, 3, 1, "先行事例調査", "海外論文のリサーチ",
        "2020-01-29 00:00:00+09", "2020-01-29 00:00:00+09", false
      ),
      Todo(
        3, 3, 3, 1, "経緯をまとめる", "報告書作成",
        "2020-01-29 00:00:00+09", "2020-01-29 00:00:00+09", false
      )
    )
  }

  override fun find(todoId: Int): Todo {
    return when (todoId) {
      1 -> Todo(
        1, 1, 1, 1, "四次元方程式を解く", "時空間４次元を表現する数式",
        "2020-01-29 00:00:00+09", "2020-01-29 00:00:00+09", false
      )
      else -> Todo()
    }
  }

  override fun findByUserAndQuery(
    userId: Int,
    projectId: Int?,
    minutesId: Int?,
    status: Boolean?,
    limit: Int?,
    offset: Int?
  ): List<Todo> {
    return listOf(
      Todo(
        1, 1, 1, 1, "四次元方程式を解く", "時空間４次元を表現する数式",
        "2020-01-29 00:00:00+09", "2020-01-29 00:00:00+09", false
      ),
      Todo(
        2, 3, 3, 1, "先行事例調査", "海外論文のリサーチ",
        "2020-01-29 00:00:00+09", "2020-01-29 00:00:00+09", false
      ),
      Todo(
        3, 3, 3, 1, "経緯をまとめる", "報告書作成",
        "2020-01-29 00:00:00+09", "2020-01-29 00:00:00+09", false
      )
    )
  }

  override fun count(userId: Int, projectId: Int?, minutesId: Int?, status: Boolean?): Int {
    return 3
  }

  override fun insert(todo: Todo) {}
  override fun insertMulti(todoList: List<Todo>) {}
  override fun update(todo: Todo) {}
  override fun delete(todoId: Int) {}
  override fun delete(todoId: Int, userId: Int) {}
}
