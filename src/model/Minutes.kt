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

data class MinutesAll(
  val minutes_id: Int,
  val user_id: Int,
  val user_name: String,
  val project_name: String,
  val client: String,
  val place: String,
  val theme: String,
  val summary: String,
  val body_text: String,
  val picture_path: List<String>,
  val attendee_name: List<String>,
  val attendee_organization: List<String>,
  val time_stamp: String
) {
  constructor() : this(
    0, 0, "", "", "", "", "", "",
    "", listOf<String>(), listOf<String>(), listOf<String>(), ""
  )
}

interface IMinutesService {
  fun find(): List<Minutes>
  fun find(minutesId: Int): Minutes
  fun findByUser(userId: Int): List<Minutes>
  fun <T> findByOperator(
    filterOp: (minutes: Minutes) -> Boolean,
    mapperOp: (minutes: Minutes) -> T,
    takeNum: Int
  ): List<T>

  fun insert(minutes: Minutes)
  fun update(minutes: Minutes)
  fun delete(minutesId: Int)
  fun delete(userId: Int, minutesId: Int)
}

interface IMinutesSummaryService {
  fun findByUserAndQuery(userId: Int, projectId: Int?, limit: Int?, offset: Int?): List<MinutesSummary>
  fun count(userId: Int): Int
  fun count(userId: Int, projectId: Int): Int
}

interface IMinutesAllService {
  fun find(minutesId: Int): MinutesAll
}
