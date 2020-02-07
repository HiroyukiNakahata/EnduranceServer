package com.endurance.service

import com.endurance.model.IMinutesSummaryService
import com.endurance.model.MinutesSummary
import org.intellij.lang.annotations.Language
import java.sql.ResultSet

class MinutesSummaryService : IMinutesSummaryService {

  override fun findByUserAndQuery(userId: Int, projectId: Int?, limit: Int?, offset: Int?): List<MinutesSummary> {
    val query = selectQuery + """
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
    val query = countQuery + "WHERE user_id = ?"

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
    val query = countQuery + "WHERE user_id = ? AND project_id = ?"

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


  @Language("SQL")
  private val selectQuery = """
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
  """

  @Language("SQL")
  private val countQuery = """
    SELECT COUNT(*)
    FROM minutes
  """
}


class MinutesSummaryServiceStub : IMinutesSummaryService {

  override fun findByUserAndQuery(userId: Int, projectId: Int?, limit: Int?, offset: Int?): List<MinutesSummary> {
    return listOf(
      MinutesSummary(
        1,
        "Hiroyuki Nakahata",
        "リーマン幾何学",
        "リーマン研究所",
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "2020-01-29 00:00:00+09"
      ),
      MinutesSummary(
        2,
        "David Hilbert",
        "複素多様体",
        "複素研究所",
        "Shibuya",
        "複素多様体の応用分野",
        "エレガントな証明とその応用",
        "2020-01-29 00:00:00+09"
      )
    )
  }

  override fun count(userId: Int): Int {
    return 2
  }

  override fun count(userId: Int, projectId: Int): Int {
    return 1
  }
}
