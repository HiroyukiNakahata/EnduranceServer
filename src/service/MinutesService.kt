package com.endurance.service

import com.endurance.model.IMinutesService
import com.endurance.model.IMinutesSummaryService
import com.endurance.model.Minutes
import com.endurance.model.MinutesSummary
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


class MinutesSummaryService : IMinutesSummaryService {
  override fun find(): List<MinutesSummary> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT m.minutes_id,
               u.family_name,
               p.project_name,
               p.client,
               m.place,
               m.theme,
               m.summary,
               m.time_stamp
        FROM minutes m 
        INNER JOIN project p USING (project_id)
        INNER JOIN "user" u USING (user_id)
        ORDER BY m.minutes_id
      """
      ).use { ps ->
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToSummary(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun find(limit: Int, offset: Int): List<MinutesSummary> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT m.minutes_id,
               u.family_name,
               p.project_name,
               p.client,
               m.place,
               m.theme,
               m.summary,
               m.time_stamp
        FROM minutes m 
        INNER JOIN project p USING (project_id)
        INNER JOIN "user" u USING (user_id)
        ORDER BY m.minutes_id
        LIMIT ? OFFSET ?
      """
      ).use { ps ->
        ps.run {
          setInt(1, limit)
          setInt(2, offset)
          executeQuery().use { rows ->
            return generateSequence {
              when {
                rows.next() -> rowsToSummary(rows)
                else -> null
              }
            }.toList()
          }
        }
      }
    }
  }

  override fun findByUser(userId: Int): List<MinutesSummary> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT m.minutes_id,
               u.family_name,
               p.project_name,
               p.client,
               m.place,
               m.theme,
               m.summary,
               m.time_stamp
        FROM minutes m 
        INNER JOIN project p USING (project_id)
        INNER JOIN "user" u USING (user_id)
        WHERE u.user_id = ?
        ORDER BY m.minutes_id
      """
      ).use { ps ->
        ps.run {
          setInt(1, userId)
          executeQuery().use { rows ->
            return generateSequence {
              when {
                rows.next() -> rowsToSummary(rows)
                else -> null
              }
            }.toList()
          }
        }
      }
    }
  }

  override fun findByUser(userId: Int, limit: Int, offset: Int): List<MinutesSummary> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT m.minutes_id,
               u.family_name,
               p.project_name,
               p.client,
               m.place,
               m.theme,
               m.summary,
               m.time_stamp
        FROM minutes m 
        INNER JOIN project p USING (project_id)
        INNER JOIN "user" u USING (user_id)
        WHERE u.user_id = ?
        ORDER BY m.minutes_id
        LIMIT ? OFFSET ?
      """
      ).use { ps ->
        ps.run {
          setInt(1, userId)
          setInt(2, limit)
          setInt(3, offset)
          executeQuery().use { rows ->
            return generateSequence {
              when {
                rows.next() -> rowsToSummary(rows)
                else -> null
              }
            }.toList()
          }
        }
      }
    }
  }

  override fun findByProject(projectId: Int): List<MinutesSummary> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT m.minutes_id,
               u.family_name,
               p.project_name,
               p.client,
               m.place,
               m.theme,
               m.summary,
               m.time_stamp
        FROM minutes m 
        INNER JOIN project p USING (project_id)
        INNER JOIN "user" u USING (user_id)
        WHERE p.project_id = ?
        ORDER BY m.minutes_id
      """
      ).use { ps ->
        ps.run {
          setInt(1, projectId)
          executeQuery().use { rows ->
            return generateSequence {
              when {
                rows.next() -> rowsToSummary(rows)
                else -> null
              }
            }.toList()
          }
        }
      }
    }
  }

  override fun findByProject(projectId: Int, limit: Int, offset: Int): List<MinutesSummary> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT m.minutes_id,
               u.family_name,
               p.project_name,
               p.client,
               m.place,
               m.theme,
               m.summary,
               m.time_stamp
        FROM minutes m 
        INNER JOIN project p USING (project_id)
        INNER JOIN "user" u USING (user_id)
        WHERE p.project_id = ?
        ORDER BY m.minutes_id
        LIMIT ? OFFSET ?
      """
      ).use { ps ->
        ps.run {
          setInt(1, projectId)
          setInt(2, limit)
          setInt(3, offset)
          executeQuery().use { rows ->
            return generateSequence {
              when {
                rows.next() -> rowsToSummary(rows)
                else -> null
              }
            }.toList()
          }
        }
      }
    }
  }

  private fun rowsToSummary(rows: ResultSet): MinutesSummary = MinutesSummary(
    rows.getInt(1),
    rows.getString(2),
    rows.getString(3),
    rows.getString(4),
    rows.getString(5),
    rows.getString(6),
    rows.getString(7),
    rows.getString(8)
  )
}
