package com.endurance.service

import com.endurance.model.IMinutesSummaryService
import com.endurance.model.MinutesSummary
import org.intellij.lang.annotations.Language
import java.sql.ResultSet

class MinutesSummaryService : IMinutesSummaryService {

  override fun findByUserAndQuery(userId: Int, projectId: Int?, limit: Int?, offset: Int?): List<MinutesSummary> {
    @Language("SQL")
    val query = """
      SELECT m.minutes_id,
             u.first_name || ' ' || u.last_name AS name,
             p.project_name,
             p.client,
             m.place,
             m.theme,
             m.summary,
             m.time_stamp
      FROM minutes m 
      INNER JOIN project p USING (project_id)
      INNER JOIN users u USING (user_id)
        WHERE u.user_id = ?
        ${projectId?.let { "AND p.project_id = ? " } ?: ""}
          ORDER BY m.minutes_id
          ${limit?.let { "LIMIT ? " } ?: ""}
          ${offset?.let { "OFFSET ? " } ?: ""}
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          listOfNotNull(userId, projectId, limit, offset)
            .forEachIndexed { idx, p ->
              setInt(idx + 1, p)
            }
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

  override fun count(userId: Int): Int {
    @Language("SQL")
    val query = """
      SELECT COUNT(*)
      FROM minutes
      WHERE user_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, userId)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rows.getInt(1)
            else -> 0
          }
        }
      }
    }
  }

  override fun count(userId: Int, projectId: Int): Int {
    @Language("SQL")
    val query = """
      SELECT COUNT(*)
      FROM minutes
      WHERE user_id = ? AND project_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, userId)
        ps.setInt(2, projectId)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rows.getInt(1)
            else -> 0
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
