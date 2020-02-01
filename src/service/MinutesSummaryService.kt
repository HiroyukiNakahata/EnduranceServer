package com.endurance.service

import com.endurance.model.IMinutesSummaryService
import com.endurance.model.MinutesSummary
import java.sql.ResultSet

class MinutesSummaryService : IMinutesSummaryService {
  override fun find(): List<MinutesSummary> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
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


class MinutesSummaryServiceStub : IMinutesSummaryService {
  override fun find(): List<MinutesSummary> {
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

  override fun find(limit: Int, offset: Int): List<MinutesSummary> {
    return listOf(
      MinutesSummary(
        1,
        "Limit Offset",
        "リーマン幾何学",
        "リーマン研究所",
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "2020-01-29 00:00:00+09"
      )
    )
  }

  override fun findByUser(userId: Int): List<MinutesSummary> {
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
        3,
        "Hiroyuki Nakahata",
        "代数的整数論",
        "代数学プログラム",
        "Yoyogi",
        "代数的整数論の発展",
        "類対論の進展",
        "2020-01-29 00:00:00+09"
      )
    )
  }

  override fun findByUser(userId: Int, limit: Int, offset: Int): List<MinutesSummary> {
    return listOf(
      MinutesSummary(
        1,
        "Limit Offset",
        "リーマン幾何学",
        "リーマン研究所",
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "2020-01-29 00:00:00+09"
      )
    )
  }

  override fun findByProject(projectId: Int): List<MinutesSummary> {
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
      )
    )
  }

  override fun findByProject(projectId: Int, limit: Int, offset: Int): List<MinutesSummary> {
    return listOf(
      MinutesSummary(
        1,
        "Limit Offset",
        "リーマン幾何学",
        "リーマン研究所",
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "2020-01-29 00:00:00+09"
      )
    )
  }
}
