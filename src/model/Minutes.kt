package com.endurance.model

data class Minutes(
  val minutes_id: Int,
  val user_id: Int,
  val project_id: Int,
  val place: String,
  val theme: String,
  val summary: String,
  val body_text: String,
  val time_stamp: String
)

interface IMinutes {
  fun findMinutes(): List<Minutes>
  fun findMinutes(id: Int): Minutes
  fun insertMinutes(minutes: Minutes)
  fun updateMinutes(minutes: Minutes)
  fun deleteMinutes(id: Int)
}
