package com.endurance.service

import com.endurance.model.ITodoService
import com.endurance.model.Todo
import org.intellij.lang.annotations.Language
import java.sql.ResultSet
import java.sql.SQLException

class TodoService : ITodoService {
  override fun find(): List<Todo> {
    val query = selectQuery + "ORDER BY todo_id"

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToTodo(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun find(todoId: Int): Todo {
    val query = selectQuery + "WHERE todo_id = ?"

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, todoId)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rowsToTodo(rows)
            else -> Todo()
          }
        }
      }
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

    val query = selectQuery + """
      WHERE user_id = ?
      ${projectId?.let { "AND project_id = ? " } ?: ""}
      ${minutesId?.let { "AND minutes_id = ? " } ?: ""}
      ${status?.let { "AND status = ? " } ?: ""}
      ORDER BY todo_id
      ${limit?.let { "LIMIT ? " } ?: ""}
      ${offset?.let { "OFFSET ? " } ?: ""}
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          listOfNotNull(userId, projectId, minutesId, status, limit, offset)
            .forEachIndexed { idx, p ->
              when (p) {
                is Int -> setInt(idx + 1, p)
                is Boolean -> setBoolean(idx + 1, p)
              }
            }
          executeQuery().use { rows ->
            return generateSequence {
              when {
                rows.next() -> rowsToTodo(rows)
                else -> null
              }
            }.toList()
          }
        }
      }
    }
  }

  override fun count(userId: Int, projectId: Int?, minutesId: Int?, status: Boolean?): Int {
    val query = countQuery + """
      WHERE user_id = ?
      ${projectId?.let { "AND project_id = ? " } ?: ""}
      ${minutesId?.let { "AND minutes_id = ? " } ?: ""}
      ${status?.let { "AND status = ?" } ?: ""}
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          listOfNotNull(userId, projectId, minutesId, status)
            .forEachIndexed { idx, p ->
              when (p) {
                is Int -> setInt(idx + 1, p)
                is Boolean -> setBoolean(idx + 1, p)
              }
            }
          executeQuery().use { rows ->
            return when {
              rows.next() -> rows.getInt(1)
              else -> 0
            }
          }
        }
      }
    }
  }

  override fun insert(todo: Todo) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(insertQuery).use { ps ->
        ps.run {
          setInt(1, todo.minutes_id)
          setInt(2, todo.project_id)
          setInt(3, todo.user_id)
          setString(4, todo.task_title)
          setString(5, todo.task_body)
          setString(6, todo.start_time_stamp)
          setString(7, todo.end_time_stamp)
          setBoolean(8, todo.status)
          execute()
        }
      }
    }
  }

  override fun insertMulti(todoList: List<Todo>) {
    HikariService.getConnection().use { con ->
      try {
        con.autoCommit = false
        con.prepareStatement(insertQuery).use { ps ->
          ps.run {
            todoList.forEach { todo ->
              setInt(1, todo.minutes_id)
              setInt(2, todo.project_id)
              setInt(3, todo.user_id)
              setString(4, todo.task_title)
              setString(5, todo.task_body)
              setString(6, todo.start_time_stamp)
              setString(7, todo.end_time_stamp)
              setBoolean(8, todo.status)
              addBatch()
            }
            executeBatch()
            con.commit()
          }
        }
      } catch (e: SQLException) {
        con.rollback()
        throw e
      }
    }
  }

  override fun update(todo: Todo) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(updateQuery).use { ps ->
        ps.run {
          setInt(1, todo.minutes_id)
          setInt(2, todo.project_id)
          setInt(3, todo.user_id)
          setString(4, todo.task_title)
          setString(5, todo.task_body)
          setString(6, todo.start_time_stamp)
          setString(7, todo.end_time_stamp)
          setBoolean(8, todo.status)
          setInt(9, todo.todo_id)
          execute()
        }
      }
    }
  }

  override fun delete(todoId: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(deleteQuery).use { ps ->
        ps.run {
          setInt(1, todoId)
          execute()
        }
      }
    }
  }

  override fun delete(todoId: Int, userId: Int) {
    val query = deleteQuery + "AND user_id = ?"

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, todoId)
          setInt(2, userId)
          execute()
        }
      }
    }
  }

  private fun rowsToTodo(rows: ResultSet): Todo = Todo(
    rows.getInt(1),
    rows.getInt(2),
    rows.getInt(3),
    rows.getInt(4),
    rows.getString(5),
    rows.getString(6),
    rows.getString(7),
    rows.getString(8),
    rows.getBoolean(9)
  )


  @Language("SQL")
  private val selectQuery = """
    SELECT todo_id,
           minutes_id,
           project_id,
           user_id,
           task_title,
           task_body,
           start_time_stamp,
           end_time_stamp,
           status
    FROM todo
  """

  @Language("SQL")
  private val countQuery = """
    SELECT COUNT(*)
    FROM todo
  """

  @Language("SQL")
  private val insertQuery = """
    INSERT INTO todo(
        minutes_id,
        project_id,
        user_id,
        task_title,
        task_body,
        start_time_stamp,
        end_time_stamp,
        status) 
    VALUES (?, ?, ?, ?, ?, 
    to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS'), 
    to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS'),
    ?)
  """

  @Language("SQL")
  private val updateQuery = """
    UPDATE todo
    SET minutes_id = ?,
        project_id = ?,
        user_id = ?,
        task_title = ?,
        task_body = ?,
        start_time_stamp = to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS'),
        end_time_stamp = to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS'),
        status = ?
    WHERE todo_id = ?
  """

  @Language("SQL")
  private val deleteQuery = """
    DELETE FROM todo
    WHERE todo_id = ?
  """
}


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
