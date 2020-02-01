package com.endurance.service

import com.endurance.model.Attendee
import com.endurance.model.IAttendeeService
import java.sql.ResultSet
import java.sql.SQLException

class AttendeeService : IAttendeeService {
  override fun find(): List<Attendee> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT attendee_id,
               minutes_id,
               attendee_name,
               organization
        FROM attendee
        ORDER BY attendee_id
        """
      ).use { ps ->
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

  override fun find(id: Int): Attendee {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT attendee_id,
               minutes_id,
               attendee_name,
               organization
        FROM attendee
        WHERE attendee_id = ?
      """
      ).use { ps ->
        ps.setInt(1, id)
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
      con.prepareStatement(
        """
        INSERT INTO attendee(minutes_id, attendee_name, organization) 
        VALUES (?, ?, ?)
      """
      ).use { ps ->
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
        con.prepareStatement(
          """
            INSERT INTO attendee(minutes_id, attendee_name, organization) 
            VALUES (?, ?, ?)
          """
        ).use { ps ->
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
      con.prepareStatement(
        """
        UPDATE attendee
        SET minutes_id = ?, attendee_name = ?, organization = ?
        WHERE attendee_id = ?
      """
      ).use { ps ->
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

  override fun delete(id: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        DELETE FROM attendee
        WHERE attendee_id = ?
      """
      ).use { ps ->
        ps.run {
          setInt(1, id)
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

  override fun find(id: Int): Attendee {
    return when (id) {
      1 -> Attendee(
        1, 1, "sample", "sample.inc"
      )
      else -> Attendee()
    }
  }

  override fun insert(attendee: Attendee) {}
  override fun insertMulti(attendees: List<Attendee>) {}
  override fun update(attendee: Attendee) {}
  override fun delete(id: Int) {}
}
