package com.endurance.service

import com.endurance.model.Todo
import com.endurance.model.ITodoService


class TodoService : ITodoService {
  override fun findTodo(): List<Todo> {
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
          val todo = mutableListOf<Todo>()
          while (rows.next()) todo.add(
            Todo(
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
          )
          return todo
        }
      }
    }
  }

  override fun findTodo(id: Int): Todo {
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
            rows.next() -> Todo(
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
            else -> Todo()
          }
        }
      }
    }
  }

  override fun insertTodo(todo: Todo) {
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

  override fun updateTodo(todo: Todo) {
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

  override fun deleteTodo(id: Int) {
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
}
