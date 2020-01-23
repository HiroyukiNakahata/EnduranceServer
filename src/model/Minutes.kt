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
) {
  constructor() : this(0, 0, 0, "", "", "", "", "")
}

data class MinutesSummary(
  val minutes_id: Int,
  val user_name: String,
  val project_name: String,
  val client: String,
  val place: String,
  val theme: String,
  val summary: String,
  val time_stamp: String
)

interface IMinutesService {
  fun find(): List<Minutes>
  fun find(id: Int): Minutes
  fun insert(minutes: Minutes)
  fun update(minutes: Minutes)
  fun delete(id: Int)
}

interface IMinutesSummaryService {
  fun find(): List<MinutesSummary>
  fun find(limit: Int, offset: Int): List<MinutesSummary>
  fun findByUser(userId: Int): List<MinutesSummary>
  fun findByUser(userId: Int, limit: Int, offset: Int): List<MinutesSummary>
  fun findByProject(projectId: Int): List<MinutesSummary>
  fun findByProject(projectId: Int, limit: Int, offset: Int): List<MinutesSummary>
}
