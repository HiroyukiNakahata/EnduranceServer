package com.endurance.service

import com.endurance.model.IMinutesAllService
import com.endurance.model.MinutesAll
import java.sql.ResultSet

class MinutesAllService : IMinutesAllService {
  override fun find(): List<MinutesAll> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT m1.minutes_id,
               u.first_name || ' ' || u.last_name AS user_name,
               pr.project_name,
               pr.client,
               m1.place,
               m1.theme,
               m1.summary,
               m1.body_text,
               m2.picture_path,
               m1.attendee_name,
               m1.attendee_organization,
               m1.time_stamp
        FROM (SELECT MAX(mi.minutes_id)         AS minutes_id,
                     MAX(mi.user_id)            AS user_id,
                     MAX(mi.project_id)         AS project_id,
                     MAX(mi.place)              AS place,
                     MAX(mi.theme)              AS theme,
                     MAX(mi.summary)            AS summary,
                     MAX(mi.body_text)          AS body_text,
                     MAX(mi.time_stamp)         AS time_stamp,
                     ARRAY_AGG(a.attendee_name) AS attendee_name,
                     ARRAY_AGG(a.organization)  AS attendee_organization
              FROM minutes AS mi
                       LEFT OUTER JOIN attendee a USING (minutes_id)
              GROUP BY mi.minutes_id) AS m1
                 INNER JOIN (SELECT MAX(mi2.minutes_id)       AS minutes_id,
                                    ARRAY_AGG(p.picture_path) AS picture_path
                             FROM minutes AS mi2
                                      LEFT OUTER JOIN picture p USING (minutes_id)
                             GROUP BY mi2.minutes_id) AS m2
                            USING (minutes_id)
                 INNER JOIN project pr USING (project_id)
                 INNER JOIN users u USING (user_id)
                 ORDER BY m1.minutes_id
      """
      ).use { ps ->
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToMinutesAll(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun find(id: Int): MinutesAll {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT m1.minutes_id,
               u.first_name || ' ' || u.last_name AS user_name,
               pr.project_name,
               pr.client,
               m1.place,
               m1.theme,
               m1.summary,
               m1.body_text,
               m2.picture_path,
               m1.attendee_name,
               m1.attendee_organization,
               m1.time_stamp
        FROM (SELECT MAX(mi.minutes_id)         AS minutes_id,
                     MAX(mi.user_id)            AS user_id,
                     MAX(mi.project_id)         AS project_id,
                     MAX(mi.place)              AS place,
                     MAX(mi.theme)              AS theme,
                     MAX(mi.summary)            AS summary,
                     MAX(mi.body_text)          AS body_text,
                     MAX(mi.time_stamp)         AS time_stamp,
                     ARRAY_AGG(a.attendee_name) AS attendee_name,
                     ARRAY_AGG(a.organization)  AS attendee_organization
              FROM minutes AS mi
                       LEFT OUTER JOIN attendee a USING (minutes_id)
              GROUP BY mi.minutes_id) AS m1
                 INNER JOIN (SELECT MAX(mi2.minutes_id)       AS minutes_id,
                                    ARRAY_AGG(p.picture_path) AS picture_path
                             FROM minutes AS mi2
                                      LEFT OUTER JOIN picture p USING (minutes_id)
                             GROUP BY mi2.minutes_id) AS m2
                            USING (minutes_id)
                 INNER JOIN project pr USING (project_id)
                 INNER JOIN users u USING (user_id)
        WHERE m1.minutes_id = ?
        """
      ).use { ps ->
        ps.setInt(1, id)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rowsToMinutesAll(rows)
            else -> MinutesAll()
          }
        }
      }
    }
  }

  private fun rowsToMinutesAll(rows: ResultSet): MinutesAll = MinutesAll(
    rows.getInt(1),
    rows.getString(2),
    rows.getString(3),
    rows.getString(4),
    rows.getString(5),
    rows.getString(6),
    rows.getString(7),
    rows.getString(8),
    (rows.getArray(9).array as? Array<out Any?>)?.filterIsInstance<String>()?.toList() ?: listOf(),
    (rows.getArray(10).array as? Array<out Any?>)?.filterIsInstance<String>()?.toList() ?: listOf(),
    (rows.getArray(11).array as? Array<out Any?>)?.filterIsInstance<String>()?.toList() ?: listOf(),
    rows.getString(12)
  )
}


class MinutesAllServiceStub : IMinutesAllService {
  override fun find(): List<MinutesAll> {
    return listOf(
      MinutesAll(
        1,
        "test",
        "test",
        "test",
        "test",
        "test",
        "test",
        "test",
        listOf("sample.jpg"),
        listOf("sample"),
        listOf("sample"),
        "2020-01-23 12:14:47"
      )
    )
  }

  override fun find(id: Int): MinutesAll {
    return when (id) {
      1 -> MinutesAll(
        1,
        "test",
        "test",
        "test",
        "test",
        "test",
        "test",
        "test",
        listOf("sample.jpg"),
        listOf("sample"),
        listOf("sample"),
        "2020-01-23 12:14:47"
      )
      else -> MinutesAll()
    }
  }
}
