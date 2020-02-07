package com.endurance.service

import com.endurance.model.Attendee
import com.endurance.model.IAttendeeService
import org.intellij.lang.annotations.Language
import java.sql.ResultSet
import java.sql.SQLException

class AttendeeService : IAttendeeService {
  override fun find(): List<Attendee> {
    val query = selectQuery + "ORDER BY attendee_id"

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToAttendee(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun find(attendeeId: Int): Attendee {
    val query = selectQuery + "WHERE attendee_id = ?"

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, attendeeId)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rowsToAttendee(rows)
            else -> Attendee()
          }
        }
      }
    }
  }

  override fun findByUser(userId: Int): List<Attendee> {
    val query = selectQueryJoin + """
        WHERE m.user_id = ?
        ORDER BY attendee_id
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, userId)
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToAttendee(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun findByUser(userId: Int, attendeeId: Int): Attendee {
    val query = selectQueryJoin + "WHERE m.user_id = ? AND attendee_id = ?"

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, userId)
        ps.setInt(2, attendeeId)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rowsToAttendee(rows)
            else -> Attendee()
          }
        }
      }
    }
  }

  override fun insert(attendee: Attendee) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(insertQuery).use { ps ->
        ps.run {
          setInt(1, attendee.minutes_id)
          setString(2, attendee.attendee_name)
          setString(3, attendee.organization)
          execute()
        }
      }
    }
  }

  override fun insertMulti(attendees: List<Attendee>) {
    HikariService.getConnection().use { con ->
      try {
        con.autoCommit = false
        con.prepareStatement(insertQuery).use { ps ->
          ps.run {
            attendees.forEach { attendee ->
              setInt(1, attendee.minutes_id)
              setString(2, attendee.attendee_name)
              setString(3, attendee.organization)
              addBatch()
            }
            executeBatch()
            con.commit()
          }
        }
      } catch (e: SQLException) {
        con.rollback()
        throw e
      }
    }
  }

  override fun update(attendee: Attendee) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(updateQuery).use { ps ->
        ps.run {
          setInt(1, attendee.minutes_id)
          setString(2, attendee.attendee_name)
          setString(3, attendee.organization)
          setInt(4, attendee.attendee_id)
          execute()
        }
      }
    }
  }

  override fun delete(attendeeId: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(deleteQuery).use { ps ->
        ps.run {
          setInt(1, attendeeId)
          execute()
        }
      }
    }
  }

  override fun delete(userId: Int, attendeeId: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(deleteQueryJoin).use { ps ->
        ps.run {
          setInt(1, attendeeId)
          setInt(2, attendeeId)
          setInt(3, userId)
          execute()
        }
      }
    }
  }

  private fun rowsToAttendee(rows: ResultSet): Attendee = Attendee(
    rows.getInt(1),
    rows.getInt(2),
    rows.getString(3),
    rows.getString(4)
  )


  @Language("SQL")
  private val selectQuery = """
    SELECT attendee_id,
           minutes_id,
           attendee_name,
           organization
    FROM attendee
  """

  @Language("SQL")
  private val selectQueryJoin = """
    SELECT attendee_id,
           minutes_id,
           attendee_name,
           organization
    FROM attendee
     INNER JOIN minutes m USING (minutes_id)
  """

  @Language("SQL")
  private val insertQuery = """
    INSERT INTO attendee(minutes_id, attendee_name, organization) 
    VALUES (?, ?, ?)
  """

  @Language("SQL")
  private val updateQuery = """
    UPDATE attendee
    SET minutes_id = ?, attendee_name = ?, organization = ?
    WHERE attendee_id = ?
  """

  @Language("SQL")
  private val deleteQuery = """
    DELETE FROM attendee
    WHERE attendee_id = ?
  """

  @Language("SQL")
  private val deleteQueryJoin = """
    DELETE FROM attendee
    WHERE attendee_id = ? AND (
    SELECT user_id
     FROM attendee
      INNER JOIN minutes
       USING (minutes_id)
       WHERE attendee_id = ?) = ?
  """
}


class AttendeeServiceStub : IAttendeeService {
  override fun find(): List<Attendee> {
    return listOf(
      Attendee(
        1, 1, "sample", "sample.inc"
      ),
      Attendee(
        2, 3, "testAttendee", "testAttendee.inc"
      )
    )
  }

  override fun find(attendeeId: Int): Attendee {
    return when (attendeeId) {
      1 -> Attendee(
        1, 1, "sample", "sample.inc"
      )
      else -> Attendee()
    }
  }

  override fun findByUser(userId: Int): List<Attendee> {
    return listOf(
      Attendee(
        1, 1, "sample", "sample.inc"
      ),
      Attendee(
        2, 3, "testAttendee", "testAttendee.inc"
      )
    )
  }

  override fun findByUser(userId: Int, attendeeId: Int): Attendee {
    return when (attendeeId) {
      1 -> Attendee(
        1, 1, "sample", "sample.inc"
      )
      else -> Attendee()
    }
  }

  override fun insert(attendee: Attendee) {}
  override fun insertMulti(attendees: List<Attendee>) {}
  override fun update(attendee: Attendee) {}
  override fun delete(attendeeId: Int) {}
  override fun delete(userId: Int, attendeeId: Int) {}
}
