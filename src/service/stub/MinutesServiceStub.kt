package com.endurance.service.stub

import com.endurance.model.IMinutesService
import com.endurance.model.Minutes

class MinutesServiceStub : IMinutesService {
  override fun find(): List<Minutes> {
    return listOf(
      Minutes(
        1,
        1,
        1,
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "物理学からのアプローチ",
        "2020-01-23 12:14:47"
      ),
      Minutes(
        2,
        2,
        2,
        "Shibuya",
        "複素多様体の応用分野",
        "エレガントな証明とその応用",
        "量子力学との親和性",
        "2020-01-29 00:00:00.0"
      )
    )
  }

  override fun find(minutesId: Int): Minutes {
    return when (minutesId) {
      1 -> Minutes(
        1,
        1,
        1,
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "物理学からのアプローチ",
        "2020-01-23 12:14:47"
      )
      else -> Minutes()
    }
  }

  override fun findByUser(userId: Int): List<Minutes> {
    return listOf(
      Minutes(
        1,
        1,
        1,
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "物理学からのアプローチ",
        "2020-01-23 12:14:47"
      ),
      Minutes(
        3,
        1,
        3,
        "Yoyogi",
        "代数的整数論の発展",
        "類対論の進展",
        "平方剰余の相互法則の拡張",
        "2020-01-29 00:00:00.0"
      )
    )
  }

  override fun <T> findByOperator(
    filterOp: (minutes: Minutes) -> Boolean,
    mapperOp: (minutes: Minutes) -> T,
    takeNum: Int
  ): List<T> {
    return listOf<T>()
  }

  override fun insert(minutes: Minutes) {}
  override fun update(minutes: Minutes) {}
  override fun delete(minutesId: Int) {}
  override fun delete(userId: Int, minutesId: Int) {}
}
