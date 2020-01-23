package com.endurance.service

import com.endurance.model.IAttendeeService
import com.endurance.model.Attendee


class AttendeeService : IAttendeeService {
  override fun findAttendee(): List<Attendee> {
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
              rows.next() -> Attendee(
                rows.getInt(1),
                rows.getInt(2),
                rows.getString(3),
                rows.getString(4)
              )
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun findAttendee(id: Int): Attendee {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT attendee_id, minutes_id, attendee_name, organization
        FROM attendee
        WHERE attendee_id = ?
      """
      ).use { ps ->
        ps.setInt(1, id)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> Attendee(
              rows.getInt(1),
              rows.getInt(2),
              rows.getString(3),
              rows.getString(4)
            )
            else -> Attendee()
          }
        }
      }
    }
  }

  override fun insertAttendee(attendee: Attendee) {
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

  override fun updateAttendee(attendee: Attendee) {
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

  override fun deleteAttendee(id: Int) {
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
}
