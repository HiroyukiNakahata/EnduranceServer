package com.endurance.routing

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
import kotlin.test.Test
import kotlin.test.assertEquals

class MinutesRoutingTest {

  // GETで全件取得
  @Test
  fun testMinutes_1() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutes = Gson().fromJson(this, Array<Minutes>::class.java)
          assertEquals(1, minutes.count())
          assertEquals(1, minutes[0].minutes_id)
          assertEquals(1, minutes[0].user_id)
          assertEquals(1, minutes[0].project_id)
          assertEquals("Ebisu", minutes[0].place)
          assertEquals("おひるごはん", minutes[0].theme)
          assertEquals("おひるごはん", minutes[0].summary)
          assertEquals("おひるごはん", minutes[0].body_text)
          assertEquals("2020-01-23 12:14:47", minutes[0].time_stamp)
        }
      }
    }
  }

  // GETでID指定
  @Test
  fun testMinutes_2() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/1").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutes = Gson().fromJson(this, Minutes::class.java)
          assertEquals(1, minutes.minutes_id)
          assertEquals(1, minutes.user_id)
          assertEquals(1, minutes.project_id)
          assertEquals("Ebisu", minutes.place)
          assertEquals("おひるごはん", minutes.theme)
          assertEquals("おひるごはん", minutes.summary)
          assertEquals("おひるごはん", minutes.body_text)
          assertEquals("2020-01-23 12:14:47", minutes.time_stamp)
        }
      }
    }
  }

  // GETでID指定：異常系
  @Test
  fun testMinutes_3() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/3").apply {
        assertEquals(HttpStatusCode.NotFound, response.status())
      }
    }
  }

  // GETで全件取得（全体）
  @Test
  fun testMinutes_18() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/all").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutes = Gson().fromJson(this, Array<MinutesAll>::class.java)
          assertEquals(1, minutes.count())
          assertEquals(1, minutes[0].minutes_id)
          assertEquals("test", minutes[0].user_name)
          assertEquals("test", minutes[0].project_name)
          assertEquals("test", minutes[0].client)
          assertEquals("test", minutes[0].place)
          assertEquals("test", minutes[0].theme)
          assertEquals("test", minutes[0].summary)
          assertEquals("test", minutes[0].body_text)
          assertEquals(listOf("sample.jpg"), minutes[0].picture_path)
          assertEquals(listOf("sample"), minutes[0].attendee_name)
          assertEquals(listOf("sample"), minutes[0].attendee_organization)
          assertEquals("2020-01-23 12:14:47", minutes[0].time_stamp)
        }
      }
    }
  }

  // GETでID指定（全体）
  @Test
  fun testMinutes_19() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/all/1").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutes = Gson().fromJson(this, MinutesAll::class.java)
          assertEquals(1, minutes.minutes_id)
          assertEquals("test", minutes.user_name)
          assertEquals("test", minutes.project_name)
          assertEquals("test", minutes.client)
          assertEquals("test", minutes.place)
          assertEquals("test", minutes.theme)
          assertEquals("test", minutes.summary)
          assertEquals("test", minutes.body_text)
          assertEquals(listOf("sample.jpg"), minutes.picture_path)
          assertEquals(listOf("sample"), minutes.attendee_name)
          assertEquals(listOf("sample"), minutes.attendee_organization)
          assertEquals("2020-01-23 12:14:47", minutes.time_stamp)
        }
      }
    }
  }

  // GETでID指定（全体）：異常系
  @Test
  fun testMinutes_20() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/all/3").apply {
        assertEquals(HttpStatusCode.NotFound, response.status())
      }
    }
  }

  // GETでID指定（全体）：異常系
  @Test
  fun testMinutes_21() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/all/hoge").apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // GETで全件取得（サマリ）
  @Test
  fun testMinutes_4() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(1, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("test", minutesSummary[0].user_name)
          assertEquals("test", minutesSummary[0].project_name)
          assertEquals("test", minutesSummary[0].client)
          assertEquals("test", minutesSummary[0].place)
          assertEquals("test", minutesSummary[0].theme)
          assertEquals("test", minutesSummary[0].summary)
          assertEquals("2020-01-23 12:14:47", minutesSummary[0].time_stamp)
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
      handleRequest(HttpMethod.Get, "/api/minutes/summary?limit=$limit&offset=$offset").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(1, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("test_limit_offset", minutesSummary[0].user_name)
          assertEquals("test_limit_offset", minutesSummary[0].project_name)
          assertEquals("test_limit_offset", minutesSummary[0].client)
          assertEquals("test_limit_offset", minutesSummary[0].place)
          assertEquals("test_limit_offset", minutesSummary[0].theme)
          assertEquals("test_limit_offset", minutesSummary[0].summary)
          assertEquals("2020-01-23 12:14:47", minutesSummary[0].time_stamp)
        }
      }
    }
  }

  // GETでユーザID指定（サマリ）
  @Test
  fun testMinutes_6() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary/user/1").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(1, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("test", minutesSummary[0].user_name)
          assertEquals("test", minutesSummary[0].project_name)
          assertEquals("test", minutesSummary[0].client)
          assertEquals("test", minutesSummary[0].place)
          assertEquals("test", minutesSummary[0].theme)
          assertEquals("test", minutesSummary[0].summary)
          assertEquals("2020-01-23 12:14:47", minutesSummary[0].time_stamp)
        }
      }
    }
  }

  // GETでユーザID指定（サマリ）：異常系
  @Test
  fun testMinutes_7() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary/user/hoge").apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // GETでlimit&offset付きユーザID指定（サマリ）
  @Test
  fun testMinutes_8() {
    val limit = 4
    val offset = 0
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary/user/1?limit=$limit&offset=$offset").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(1, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("test_limit_offset", minutesSummary[0].user_name)
          assertEquals("test_limit_offset", minutesSummary[0].project_name)
          assertEquals("test_limit_offset", minutesSummary[0].client)
          assertEquals("test_limit_offset", minutesSummary[0].place)
          assertEquals("test_limit_offset", minutesSummary[0].theme)
          assertEquals("test_limit_offset", minutesSummary[0].summary)
          assertEquals("2020-01-23 12:14:47", minutesSummary[0].time_stamp)
        }
      }
    }
  }

  // GETでプロジェクトID指定（サマリ）
  @Test
  fun testMinutes_9() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary/project/1").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(1, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("test", minutesSummary[0].user_name)
          assertEquals("test", minutesSummary[0].project_name)
          assertEquals("test", minutesSummary[0].client)
          assertEquals("test", minutesSummary[0].place)
          assertEquals("test", minutesSummary[0].theme)
          assertEquals("test", minutesSummary[0].summary)
          assertEquals("2020-01-23 12:14:47", minutesSummary[0].time_stamp)
        }
      }
    }
  }

  // GETでプロジェクトID指定（サマリ）：異常系
  @Test
  fun testMinutes_10() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary/project/hoge").apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // GETでlimit&offset付きプロジェクトID指定（サマリ）
  @Test
  fun testMinutes_11() {
    val limit = 4
    val offset = 0
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/minutes/summary/project/1?limit=$limit&offset=$offset").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val minutesSummary = Gson().fromJson(this, Array<MinutesSummary>::class.java)
          assertEquals(1, minutesSummary.count())
          assertEquals(1, minutesSummary[0].minutes_id)
          assertEquals("test_limit_offset", minutesSummary[0].user_name)
          assertEquals("test_limit_offset", minutesSummary[0].project_name)
          assertEquals("test_limit_offset", minutesSummary[0].client)
          assertEquals("test_limit_offset", minutesSummary[0].place)
          assertEquals("test_limit_offset", minutesSummary[0].theme)
          assertEquals("test_limit_offset", minutesSummary[0].summary)
          assertEquals("2020-01-23 12:14:47", minutesSummary[0].time_stamp)
        }
      }
    }
  }

  // POSTで登録
  @Test
  fun testMinutes_12() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/minutes") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(
          Gson().toJson(
            Minutes(
              1,
              1,
              1,
              "Shibuya",
              "ばんごはん",
              "ばんごはん",
              "ばんごはん",
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
          assertEquals("ばんごはん", minutes.summary)
          assertEquals("ばんごはん", minutes.body_text)
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
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(
          Gson().toJson(
            Minutes(
              1,
              1,
              1,
              "Shibuya",
              "ばんごはん",
              "ばんごはん",
              "ばんごはん",
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
          assertEquals("ばんごはん", minutes.summary)
          assertEquals("ばんごはん", minutes.body_text)
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
      handleRequest(HttpMethod.Delete, "/api/minutes/1").apply {
        assertEquals(HttpStatusCode.OK, response.status())
      }
    }
  }

  // DELETEで削除：異常系
  @Test
  fun testMinutes_17() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Delete, "/api/minutes/hoge").apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }
}
