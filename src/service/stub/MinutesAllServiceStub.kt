package com.endurance.service.stub

import com.endurance.model.IMinutesAllService
import com.endurance.model.MinutesAll

class MinutesAllServiceStub : IMinutesAllService {

  override fun find(minutesId: Int): MinutesAll {
    return when (minutesId) {
      1 -> MinutesAll(
        1,
        1,
        "Hiroyuki Nakahata",
        "リーマン幾何学",
        "リーマン研究所",
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "物理学からのアプローチ",
        listOf("2020-01-29_03-42-51-594-1336023490071.png"),
        listOf("Fermat", "Leibniz"),
        listOf("harajuku.inc", "roppongi.inc"),
        "2020-01-23 12:14:47+09"
      )
      else -> MinutesAll()
    }
  }
}
