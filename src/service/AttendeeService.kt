package com.endurance.service

import com.endurance.model.Attendee
import com.endurance.model.IAttendeeService
import org.intellij.lang.annotations.Language
import java.sql.ResultSet
import java.sql.SQLException

class AttendeeService : IAttendeeService {
  override fun find(): List<Attendee> {
    @Language("SQL")
    val query = """
      SELECT attendee_id, minutes_id, attendee_name, organization
      FROM attendee
      ORDER BY attendee_id
    """

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
    @Language("SQL")
    val query = """
      SELECT attendee_id, minutes_id, attendee_name, organization
      FROM attendee
      WHERE attendee_id = ?
    """

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
    @Language("SQL")
    val query = """
      SELECT attendee_id, minutes_id, attendee_name, organization
      FROM attendee
       INNER JOIN minutes m USING (minutes_id) 
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
    @Language("SQL")
    val query = """
      SELECT attendee_id, minutes_id, attendee_name, organization
      FROM attendee
       INNER JOIN minutes m USING (minutes_id)
       WHERE m.user_id = ? AND attendee_id = ?
    """

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

  @Language("SQL")
  private val insertQuery = """
    INSERT INTO attendee(minutes_id, attendee_name, organization) 
    VALUES (?, ?, ?)
  """

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
    @Language("SQL")
    val query = """
      UPDATE attendee
      SET minutes_id = ?, attendee_name = ?, organization = ?
      WHERE attendee_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
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
    @Language("SQL")
    val query = """
      DELETE FROM attendee
      WHERE attendee_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, attendeeId)
          execute()
        }
      }
    }
  }

  override fun delete(userId: Int, attendeeId: Int) {
    @Language("SQL")
    val query = """
      DELETE FROM attendee
      WHERE attendee_id = ? AND (
      SELECT user_id
       FROM attendee
        INNER JOIN minutes
         USING (minutes_id)
         WHERE attendee_id = ?) = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
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
}
