package com.endurance.model

data class Attendee(
  val attendee_id: Int,
  val minutes_id: Int,
  val attendee_name: String,
  val organization: String
) {
  constructor() : this(0, 0, "", "")
}

interface IAttendeeService {
  fun find(): List<Attendee>
  fun find(attendeeId: Int): Attendee
  fun findByUser(userId: Int): List<Attendee>
  fun findByUser(userId: Int, attendeeId: Int): Attendee
  fun insert(attendee: Attendee)
  fun insertMulti(attendees: List<Attendee>)
  fun update(attendee: Attendee)
  fun delete(attendeeId: Int)
  fun deleteByUser(userId: Int, attendeeId: Int)
}
