package com.endurance.service.stub

import com.endurance.model.IMinutesSummaryService
import com.endurance.model.MinutesSummary

class MinutesSummaryServiceStub : IMinutesSummaryService {

  override fun findByUserAndQuery(userId: Int, projectId: Int?, limit: Int?, offset: Int?): List<MinutesSummary> {
    return listOf(
      MinutesSummary(
        1,
        "Hiroyuki Nakahata",
        "リーマン幾何学",
        "リーマン研究所",
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "2020-01-29 00:00:00+09"
      ),
      MinutesSummary(
        2,
        "David Hilbert",
        "複素多様体",
        "複素研究所",
        "Shibuya",
        "複素多様体の応用分野",
        "エレガントな証明とその応用",
        "2020-01-29 00:00:00+09"
      )
    )
  }

  override fun count(userId: Int): Int {
    return 2
  }

  override fun count(userId: Int, projectId: Int): Int {
    return 1
  }
}
