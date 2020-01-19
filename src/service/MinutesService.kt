package com.endurance.service

import com.endurance.model.IMinutesService
import com.endurance.model.Minutes
import java.sql.SQLException


class MinutesService : IMinutesService {
  override fun findMinutes(): List<Minutes> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT minutes_id, user_id, project_id, place, theme, summary, body_text, time_stamp
        FROM endurance.public.minutes
        ORDER BY minutes_id
      """
      ).use { ps ->
        ps.executeQuery().use { rows ->
          val minutes = mutableListOf<Minutes>()
          while (rows.next()) {
            minutes.add(
              Minutes(
                rows.getInt(1),
                rows.getInt(2),
                rows.getInt(3),
                rows.getString(4),
                rows.getString(5),
                rows.getString(6),
                rows.getString(7),
                rows.getString(8)
              )
            )
          }
          return minutes
        }
      }
    }
  }

  override fun findMinutes(id: Int): Minutes {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT minutes_id, user_id, project_id, place, theme, summary, body_text, time_stamp
        FROM endurance.public.minutes
        WHERE minutes_id = ?
      """
      ).use { ps ->
        ps.setInt(1, id)
        ps.executeQuery().use { rows ->
          rows.next()
          return try {
            Minutes(
              rows.getInt(1),
              rows.getInt(2),
              rows.getInt(3),
              rows.getString(4),
              rows.getString(5),
              rows.getString(6),
              rows.getString(7),
              rows.getString(8)
            )
          } catch (e: SQLException) {
            Minutes(0, 0, 0, "", "", "", "", "")
          }
        }
      }
    }
  }

  override fun insertMinutes(minutes: Minutes) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        INSERT INTO endurance.public.minutes(user_id, project_id, place, theme, summary, body_text, time_stamp)
        VALUES (?, ?, ?, ?, ?, ?, current_timestamp)
      """
      ).use { ps ->
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

  override fun updateMinutes(minutes: Minutes) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        UPDATE endurance.public.minutes
        SET user_id = ?, project_id = ?, place = ?, theme = ?, summary = ?, body_text = ?
        WHERE minutes_id = ?
      """
      ).use { ps ->
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

  override fun deleteMinutes(id: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        DELETE FROM endurance.public.minutes
        WHERE minutes_id = ?
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
