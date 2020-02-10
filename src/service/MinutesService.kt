package com.endurance.service

import com.endurance.model.IMinutesService
import com.endurance.model.Minutes
import org.intellij.lang.annotations.Language
import java.sql.ResultSet

class MinutesService : IMinutesService {

  override fun find(): List<Minutes> {
    @Language("SQL")
    val query = """
      SELECT minutes_id, user_id, project_id, place, theme, summary, body_text, time_stamp
      FROM minutes
      ORDER BY minutes_id
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToMinutes(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun find(minutesId: Int): Minutes {
    @Language("SQL")
    val query = """
      SELECT minutes_id, user_id, project_id, place, theme, summary, body_text, time_stamp
      FROM minutes
      WHERE minutes_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, minutesId)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rowsToMinutes(rows)
            else -> Minutes()
          }
        }
      }
    }
  }

  override fun findByUser(userId: Int): List<Minutes> {
    @Language("SQL")
    val query = """
      SELECT minutes_id, user_id, project_id, place, theme, summary, body_text, time_stamp
      FROM minutes
      WHERE user_id = ? ORDER BY minutes_id
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, userId)
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToMinutes(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun <T> findByOperator(
    filterOp: (minutes: Minutes) -> Boolean,
    mapperOp: (minutes: Minutes) -> T,
    takeNum: Int
  ): List<T> {
    @Language("SQL")
    val query = """
      SELECT minutes_id, user_id, project_id, place, theme, summary, body_text, time_stamp
      FROM minutes
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToMinutes(rows)
              else -> null
            }
          }.filter(filterOp).map(mapperOp).take(takeNum).toList()
        }
      }
    }
  }

  override fun insert(minutes: Minutes) {
    @Language("SQL")
    val query = """
      INSERT INTO minutes(user_id, project_id, place, theme, summary, body_text, time_stamp)
      VALUES (?, ?, ?, ?, ?, ?, current_timestamp)
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, minutes.user_id)
          setInt(2, minutes.project_id)
          setString(3, minutes.place)
          setString(4, minutes.theme)
          setString(5, minutes.summary)
          setString(6, minutes.body_text)
          execute()
        }
      }
    }
  }

  override fun update(minutes: Minutes) {
    @Language("SQL")
    val query = """
      UPDATE minutes
      SET user_id = ?, project_id = ?, place = ?, theme = ?, summary = ?, body_text = ?
      WHERE minutes_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, minutes.user_id)
          setInt(2, minutes.project_id)
          setString(3, minutes.place)
          setString(4, minutes.theme)
          setString(5, minutes.summary)
          setString(6, minutes.body_text)
          setInt(7, minutes.minutes_id)
          execute()
        }
      }
    }
  }

  override fun delete(minutesId: Int) {
    @Language("SQL")
    val query = """
      DELETE FROM minutes
      WHERE minutes_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, minutesId)
          execute()
        }
      }
    }
  }

  override fun delete(userId: Int, minutesId: Int) {
    @Language("SQL")
    val query = """
      DELETE FROM minutes
      WHERE minutes_id = ? AND user_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, minutesId)
          setInt(2, userId)
          execute()
        }
      }
    }
  }

  private fun rowsToMinutes(rows: ResultSet): Minutes = Minutes(
    rows.getInt(1),
    rows.getInt(2),
    rows.getInt(3),
    rows.getString(4),
    rows.getString(5),
    rows.getString(6),
    rows.getString(7),
    rows.getString(8)
  )
}
