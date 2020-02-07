package com.endurance.service

import com.endurance.model.IMinutesAllService
import com.endurance.model.MinutesAll
import org.intellij.lang.annotations.Language
import java.sql.ResultSet

class MinutesAllService : IMinutesAllService {
  override fun find(minutesId: Int): MinutesAll {
    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, minutesId)
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
    rows.getInt(2),
    rows.getString(3),
    rows.getString(4),
    rows.getString(5),
    rows.getString(6),
    rows.getString(7),
    rows.getString(8),
    rows.getString(9),
    (rows.getArray(10).array as? Array<out Any?>)?.filterIsInstance<String>()?.toList() ?: listOf(),
    (rows.getArray(11).array as? Array<out Any?>)?.filterIsInstance<String>()?.toList() ?: listOf(),
    (rows.getArray(12).array as? Array<out Any?>)?.filterIsInstance<String>()?.toList() ?: listOf(),
    rows.getString(13)
  )


  @Language("SQL")
  val query = """
    SELECT MAX(minutes_id)                              AS minutes_id,
           MAX(u.user_id)                               AS user_id,
           MAX(u.first_name) || ' ' || MAX(u.last_name) AS user_name,
           MAX(pr.project_name)                         AS project_name,
           MAX(pr.client)                               AS client,
           MAX(m1.place)                                AS place,
           MAX(m1.theme)                                AS theme,
           MAX(m1.summary)                              AS summary,
           MAX(m1.body_text)                            AS body_text,
           ARRAY_AGG(p.picture_path)                    AS picture_path,
           MAX(m1.attendee_name)                        AS attendee_name,
           MAX(m1.attendee_organization)                AS organization,
           MAX(m1.time_stamp)                           AS time_stamp
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
             LEFT OUTER JOIN picture p USING (minutes_id)
             INNER JOIN project pr USING (project_id)
             INNER JOIN users u USING (user_id)
    WHERE m1.minutes_id = ?
    GROUP BY minutes_id
  """
}


class MinutesAllServiceStub : IMinutesAllService {

  override fun find(minutesId: Int): MinutesAll {
    return when (minutesId) {
      1 -> MinutesAll(
        1,
        1,
        "Hiroyuki Nakahata",
        "リーマン幾何学",
        "リーマン研究所",
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "物理学からのアプローチ",
        listOf("2020-01-29_03-42-51-594-1336023490071.png"),
        listOf("Fermat", "Leibniz"),
        listOf("harajuku.inc", "roppongi.inc"),
        "2020-01-23 12:14:47+09"
      )
      else -> MinutesAll()
    }
  }
}
