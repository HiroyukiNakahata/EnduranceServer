package com.endurance.routing

import com.endurance.authentication.JwtAuth
import com.endurance.model.Minutes
import com.endurance.model.MinutesAll
import com.endurance.model.MinutesSummary
import com.endurance.module
import com.google.gson.Gson
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MinutesRoutingTest {

  private var token = ""

  @BeforeTest
  fun beforeTest() {
    token = JwtAuth.createToken(1, System.currentTimeMillis())
  }

  // GETで全件取得
  @Test
  fun testMinutes_1() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutes = Gson().fromJson(this, Array<Minutes>::class.java)
          assertEquals(2, minutes.count())
          assertEquals(1, minutes[0].minutes_id)
          assertEquals(1, minutes[0].user_id)
          assertEquals(1, minutes[0].project_id)
          assertEquals("Ebisu", minutes[0].place)
          assertEquals("リーマン幾何とその応用", minutes[0].theme)
          assertEquals("興味深い知見", minutes[0].summary)
          assertEquals("物理学からのアプローチ", minutes[0].body_text)
          assertEquals("2020-01-23 12:14:47", minutes[0].time_stamp)
          assertEquals(3, minutes[1].minutes_id)
          assertEquals(1, minutes[1].user_id)
          assertEquals(3, minutes[1].project_id)
          assertEquals("Yoyogi", minutes[1].place)
          assertEquals("代数的整数論の発展", minutes[1].theme)
          assertEquals("類対論の進展", minutes[1].summary)
          assertEquals("平方剰余の相互法則の拡張", minutes[1].body_text)
          assertEquals("2020-01-29 00:00:00.0", minutes[1].time_stamp)
        }
      }
    }
  }

  // GETでID指定
  @Test
  fun testMinutes_2() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutes = Gson().fromJson(this, Minutes::class.java)
          assertEquals(1, minutes.minutes_id)
          assertEquals(1, minutes.user_id)
          assertEquals(1, minutes.project_id)
          assertEquals("Ebisu", minutes.place)
          assertEquals("リーマン幾何とその応用", minutes.theme)
          assertEquals("興味深い知見", minutes.summary)
          assertEquals("物理学からのアプローチ", minutes.body_text)
          assertEquals("2020-01-23 12:14:47", minutes.time_stamp)
        }
      }
    }
  }

  // GETでID指定：異常系
  @Test
  fun testMinutes_3() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/3"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.NotFound, response.status())
      }
    }
  }

  // GETで全件取得（全体）
  @Test
  fun testMinutes_18() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/all"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutes = Gson().fromJson(this, Array<MinutesAll>::class.java)
          assertEquals(2, minutes.count())
          assertEquals(1, minutes[0].minutes_id)
          assertEquals("Hiroyuki Nakahata", minutes[0].user_name)
          assertEquals("リーマン幾何学", minutes[0].project_name)
          assertEquals("リーマン研究所", minutes[0].client)
          assertEquals("Ebisu", minutes[0].place)
          assertEquals("リーマン幾何とその応用", minutes[0].theme)
          assertEquals("興味深い知見", minutes[0].summary)
          assertEquals("物理学からのアプローチ", minutes[0].body_text)
          assertEquals(listOf("2020-01-29_03-42-51-594-1336023490071.png"), minutes[0].picture_path)
          assertEquals(listOf("Fermat", "Leibniz"), minutes[0].attendee_name)
          assertEquals(listOf("harajuku.inc", "roppongi.inc"), minutes[0].attendee_organization)
          assertEquals("2020-01-23 12:14:47+09", minutes[0].time_stamp)
          assertEquals(2, minutes[1].minutes_id)
          assertEquals("David Hilbert", minutes[1].user_name)
          assertEquals("複素多様体", minutes[1].project_name)
          assertEquals("複素研究所", minutes[1].client)
          assertEquals("Shibuya", minutes[1].place)
          assertEquals("複素多様体の応用分野", minutes[1].theme)
          assertEquals("エレガントな証明とその応用", minutes[1].summary)
          assertEquals("量子力学との親和性", minutes[1].body_text)
          assertEquals(listOf(), minutes[1].picture_path)
          assertEquals(listOf("Turing"), minutes[1].attendee_name)
          assertEquals(listOf("gotanda.inc"), minutes[1].attendee_organization)
          assertEquals("2020-01-23 12:14:47+09", minutes[1].time_stamp)
        }
      }
    }
  }

  // GETでID指定（全体）
  @Test
  fun testMinutes_19() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/all/1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutes = Gson().fromJson(this, MinutesAll::class.java)
          assertEquals(1, minutes.minutes_id)
          assertEquals("Hiroyuki Nakahata", minutes.user_name)
          assertEquals("リーマン幾何学", minutes.project_name)
          assertEquals("リーマン研究所", minutes.client)
          assertEquals("Ebisu", minutes.place)
          assertEquals("リーマン幾何とその応用", minutes.theme)
          assertEquals("興味深い知見", minutes.summary)
          assertEquals("物理学からのアプローチ", minutes.body_text)
          assertEquals(listOf("2020-01-29_03-42-51-594-1336023490071.png"), minutes.picture_path)
          assertEquals(listOf("Fermat", "Leibniz"), minutes.attendee_name)
          assertEquals(listOf("harajuku.inc", "roppongi.inc"), minutes.attendee_organization)
          assertEquals("2020-01-23 12:14:47+09", minutes.time_stamp)
        }
      }
    }
  }

  // GETでID指定（全体）：異常系
  @Test
  fun testMinutes_20() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/all/3"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.NotFound, response.status())
      }
    }
  }

  // GETでID指定（全体）：異常系
  @Test
  fun testMinutes_21() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/all/hoge"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // GETで全件取得（サマリ）
  @Test
  fun testMinutes_4() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(2, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("Hiroyuki Nakahata", minutesSummary[0].user_name)
          assertEquals("リーマン幾何学", minutesSummary[0].project_name)
          assertEquals("リーマン研究所", minutesSummary[0].client)
          assertEquals("Ebisu", minutesSummary[0].place)
          assertEquals("リーマン幾何とその応用", minutesSummary[0].theme)
          assertEquals("興味深い知見", minutesSummary[0].summary)
          assertEquals("2020-01-29 00:00:00+09", minutesSummary[0].time_stamp)
          assertEquals(3, minutesSummary[1].minutes_id)
          assertEquals("Hiroyuki Nakahata", minutesSummary[1].user_name)
          assertEquals("代数的整数論", minutesSummary[1].project_name)
          assertEquals("代数学プログラム", minutesSummary[1].client)
          assertEquals("Yoyogi", minutesSummary[1].place)
          assertEquals("代数的整数論の発展", minutesSummary[1].theme)
          assertEquals("類対論の進展", minutesSummary[1].summary)
          assertEquals("2020-01-29 00:00:00+09", minutesSummary[1].time_stamp)
        }
      }
    }
  }

  // GETでlimit&offset付き取得（サマリ）
  @Test
  fun testMinutes_5() {
    val limit = 4
    val offset = 0
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary?limit=$limit&offset=$offset"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(1, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("Limit Offset", minutesSummary[0].user_name)
          assertEquals("リーマン幾何学", minutesSummary[0].project_name)
          assertEquals("リーマン研究所", minutesSummary[0].client)
          assertEquals("Ebisu", minutesSummary[0].place)
          assertEquals("リーマン幾何とその応用", minutesSummary[0].theme)
          assertEquals("興味深い知見", minutesSummary[0].summary)
          assertEquals("2020-01-29 00:00:00+09", minutesSummary[0].time_stamp)
        }
      }
    }
  }

  // GETでユーザID指定（サマリ）
  @Test
  fun testMinutes_6() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary?project=1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(2, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("Hiroyuki Nakahata", minutesSummary[0].user_name)
          assertEquals("リーマン幾何学", minutesSummary[0].project_name)
          assertEquals("リーマン研究所", minutesSummary[0].client)
          assertEquals("Ebisu", minutesSummary[0].place)
          assertEquals("リーマン幾何とその応用", minutesSummary[0].theme)
          assertEquals("興味深い知見", minutesSummary[0].summary)
          assertEquals("2020-01-29 00:00:00+09", minutesSummary[0].time_stamp)
          assertEquals(3, minutesSummary[1].minutes_id)
          assertEquals("Hiroyuki Nakahata", minutesSummary[1].user_name)
          assertEquals("代数的整数論", minutesSummary[1].project_name)
          assertEquals("代数学プログラム", minutesSummary[1].client)
          assertEquals("Yoyogi", minutesSummary[1].place)
          assertEquals("代数的整数論の発展", minutesSummary[1].theme)
          assertEquals("類対論の進展", minutesSummary[1].summary)
          assertEquals("2020-01-29 00:00:00+09", minutesSummary[1].time_stamp)
        }
      }
    }
  }

  // GETでユーザID指定（サマリ）：異常系
  @Test
  fun testMinutes_7() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary?project=hoge"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(2, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("Hiroyuki Nakahata", minutesSummary[0].user_name)
          assertEquals("リーマン幾何学", minutesSummary[0].project_name)
          assertEquals("リーマン研究所", minutesSummary[0].client)
          assertEquals("Ebisu", minutesSummary[0].place)
          assertEquals("リーマン幾何とその応用", minutesSummary[0].theme)
          assertEquals("興味深い知見", minutesSummary[0].summary)
          assertEquals("2020-01-29 00:00:00+09", minutesSummary[0].time_stamp)
          assertEquals(3, minutesSummary[1].minutes_id)
          assertEquals("Hiroyuki Nakahata", minutesSummary[1].user_name)
          assertEquals("代数的整数論", minutesSummary[1].project_name)
          assertEquals("代数学プログラム", minutesSummary[1].client)
          assertEquals("Yoyogi", minutesSummary[1].place)
          assertEquals("代数的整数論の発展", minutesSummary[1].theme)
          assertEquals("類対論の進展", minutesSummary[1].summary)
          assertEquals("2020-01-29 00:00:00+09", minutesSummary[1].time_stamp)
        }
      }
    }
  }

  // GETでlimit&offset付きユーザID指定（サマリ）
  @Test
  fun testMinutes_8() {
    val limit = 4
    val offset = 0
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary?project=1&limit=$limit&offset=$offset"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(1, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("Limit Offset", minutesSummary[0].user_name)
          assertEquals("リーマン幾何学", minutesSummary[0].project_name)
          assertEquals("リーマン研究所", minutesSummary[0].client)
          assertEquals("Ebisu", minutesSummary[0].place)
          assertEquals("リーマン幾何とその応用", minutesSummary[0].theme)
          assertEquals("興味深い知見", minutesSummary[0].summary)
          assertEquals("2020-01-29 00:00:00+09", minutesSummary[0].time_stamp)
        }
      }
    }
  }

  // POSTで登録
  @Test
  fun testMinutes_12() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/minutes") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(
          Gson().toJson(
            Minutes(
              1,
              1,
              1,
              "Shibuya",
              "ばんごはん",
              "ばんごはんの内容",
              "ばんごはんのおかず",
              "2020-01-23 12:14:47"
            )
          )
        )
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutes = Gson().fromJson(this, Minutes::class.java)
          assertEquals(1, minutes.minutes_id)
          assertEquals(1, minutes.user_id)
          assertEquals(1, minutes.project_id)
          assertEquals("Shibuya", minutes.place)
          assertEquals("ばんごはん", minutes.theme)
          assertEquals("ばんごはんの内容", minutes.summary)
          assertEquals("ばんごはんのおかず", minutes.body_text)
          assertEquals("2020-01-23 12:14:47", minutes.time_stamp)
        }
      }
    }
  }

  // POSTで登録：異常系
  @Test
  fun testMinutes_13() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/minutes") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(Minutes()))
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // PUTで更新
  @Test
  fun testMinutes_14() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Put, "/api/minutes") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(
          Gson().toJson(
            Minutes(
              1,
              1,
              1,
              "Shibuya",
              "ばんごはん",
              "ばんごはんの内容",
              "ばんごはんのおかず",
              "2020-01-23 12:14:47"
            )
          )
        )
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutes = Gson().fromJson(this, Minutes::class.java)
          assertEquals(1, minutes.minutes_id)
          assertEquals(1, minutes.user_id)
          assertEquals(1, minutes.project_id)
          assertEquals("Shibuya", minutes.place)
          assertEquals("ばんごはん", minutes.theme)
          assertEquals("ばんごはんの内容", minutes.summary)
          assertEquals("ばんごはんのおかず", minutes.body_text)
          assertEquals("2020-01-23 12:14:47", minutes.time_stamp)
        }
      }
    }
  }

  // PUTで更新：異常系
  @Test
  fun testMinutes_15() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Put, "/api/minutes") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(Minutes()))
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // DELETEで削除
  @Test
  fun testMinutes_16() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Delete, "/api/minutes/1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
      }
    }
  }

  // DELETEで削除：異常系
  @Test
  fun testMinutes_17() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Delete, "/api/minutes/hoge"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }
}
