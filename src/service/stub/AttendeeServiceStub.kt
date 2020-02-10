package com.endurance.service.stub

import com.endurance.model.Attendee
import com.endurance.model.IAttendeeService

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
