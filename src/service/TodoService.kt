package com.endurance.service

import com.endurance.model.Todo
import com.endurance.model.ITodoService
import java.sql.ResultSet
import java.sql.SQLException

class TodoService : ITodoService {
  override fun find(): List<Todo> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
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
        ORDER BY todo_id
      """
      ).use { ps ->
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

  override fun find(id: Int): Todo {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
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
        WHERE todo_id = ?
      """
      ).use { ps ->
        ps.setInt(1, id)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rowsToTodo(rows)
            else -> Todo()
          }
        }
      }
    }
  }

  override fun insert(todo: Todo) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        INSERT INTO todo(
            minutes_id,
            project_id,
            user_id,
            task_title,
            task_body,
            start_time_stamp,
            end_time_stamp,
            status) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      """
      ).use { ps ->
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
        con.prepareStatement(
          """
          INSERT INTO todo(
            minutes_id,
            project_id,
            user_id,
            task_title,
            task_body,
            start_time_stamp,
            end_time_stamp,
            status) 
          VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
        ).use { ps ->
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
      con.prepareStatement(
        """
        UPDATE todo
        SET minutes_id = ?,
            project_id = ?,
            user_id = ?,
            task_title = ?,
            task_body = ?,
            start_time_stamp = ?,
            end_time_stamp = ?,
            status = ?
        WHERE todo_id = ?
      """
      ).use { ps ->
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

  override fun delete(id: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        DELETE FROM todo
        WHERE todo_id = ?
      """
      ).use { ps ->
        ps.run {
          setInt(1, id)
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
}


class TodoServiceStub : ITodoService {
  override fun find(): List<Todo> {
    return listOf(
      Todo(
        1, 1, 1, 1, "test", "test",
        "2020-01-23 12:14:47", "2020-01-23 12:14:47", false
      )
    )
  }

  override fun find(id: Int): Todo {
    return when (id) {
      1 -> Todo(
        1, 1, 1, 1, "test", "test",
        "2020-01-23 12:14:47", "2020-01-23 12:14:47", false
      )
      else -> Todo()
    }
  }

  override fun insert(todo: Todo) {}
  override fun insertMulti(todoList: List<Todo>) {}
  override fun update(todo: Todo) {}
  override fun delete(id: Int) {}
}
