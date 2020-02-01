package com.endurance.service

import com.endurance.model.IMinutesService
import com.endurance.model.Minutes
import java.sql.ResultSet

class MinutesService : IMinutesService {
  override fun find(): List<Minutes> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT minutes_id, user_id, project_id, place, theme, summary, body_text, time_stamp
        FROM endurance.public.minutes
        ORDER BY minutes_id
      """
      ).use { ps ->
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

  override fun find(id: Int): Minutes {
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
          return when {
            rows.next() -> rowsToMinutes(rows)
            else -> Minutes()
          }
        }
      }
    }
  }

  override fun insert(minutes: Minutes) {
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

  override fun update(minutes: Minutes) {
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

  override fun delete(id: Int) {
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


class MinutesServiceStub : IMinutesService {
  override fun find(): List<Minutes> {
    return listOf(
      Minutes(
        1,
        1,
        1,
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "物理学からのアプローチ",
        "2020-01-23 12:14:47"
      ),
      Minutes(
        2,
        2,
        2,
        "Shibuya",
        "複素多様体の応用分野",
        "エレガントな証明とその応用",
        "量子力学との親和性",
        "2020-01-29 00:00:00.0"
      )
    )
  }

  override fun find(id: Int): Minutes {
    return when (id) {
      1 -> Minutes(
        1,
        1,
        1,
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "物理学からのアプローチ",
        "2020-01-23 12:14:47"
      )
      else -> Minutes()
    }
  }

  override fun insert(minutes: Minutes) {}
  override fun update(minutes: Minutes) {}
  override fun delete(id: Int) {}
}
