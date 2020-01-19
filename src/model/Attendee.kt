package com.endurance.model

data class Attendee(
  val attendee_id: Int,
  val minutes_id: Int,
  val attendee_name: String,
  val organization: String
)

interface IAttendeeService {
  fun findAttendee(): List<Attendee>
  fun findAttendee(id: Int): Attendee
  fun insertAttendee(attendee: Attendee)
  fun updateAttendee(attendee: Attendee)
  fun deleteAttendee(id: Int)
}
