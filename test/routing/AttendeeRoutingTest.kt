package com.endurance.routing

import com.endurance.authentication.JwtAuth
import com.endurance.model.Attendee
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

class AttendeeRoutingTest {

  private var token = ""

  @BeforeTest
  fun beforeTest() {
    token = JwtAuth.createToken(1, System.currentTimeMillis())
  }

  // GETで全件取得
  @Test
  fun testAttendee_1() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/attendee") {
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val attendees = Gson().fromJson(this, Array<Attendee>::class.java)
          assertEquals(2, attendees.count())
          assertEquals(1, attendees[0].attendee_id)
          assertEquals(1, attendees[0].minutes_id)
          assertEquals("sample", attendees[0].attendee_name)
          assertEquals("sample.inc", attendees[0].organization)
          assertEquals(2, attendees[1].attendee_id)
          assertEquals(3, attendees[1].minutes_id)
          assertEquals("testAttendee", attendees[1].attendee_name)
          assertEquals("testAttendee.inc", attendees[1].organization)
        }
      }
    }
  }

  // GETでID指定
  @Test
  fun testAttendee_2() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/attendee/1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val attendees = Gson().fromJson(this, Attendee::class.java)
          assertEquals(1, attendees.attendee_id)
          assertEquals(1, attendees.minutes_id)
          assertEquals("sample", attendees.attendee_name)
          assertEquals("sample.inc", attendees.organization)
        }
      }
    }
  }

  // GETでID指定：異常系
  @Test
  fun testAttendee_3() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/attendee/2"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.NotFound, response.status())
      }
    }
  }

  // GETでID指定：異常系
  @Test
  fun testAttendee_4() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/attendee/hoge"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // POSTで登録
  @Test
  fun testAttendee_5() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/attendee") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(Attendee(1, 1, "sample", "sample.inc")))
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val attendee = Gson().fromJson(this, Attendee::class.java)
          assertEquals(1, attendee.attendee_id)
          assertEquals(1, attendee.minutes_id)
          assertEquals("sample", attendee.attendee_name)
          assertEquals("sample.inc", attendee.organization)
        }
      }
    }
  }

  // POSTで登録：異常系
  @Test
  fun testAttendee_6() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/attendee") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(Attendee(1, 1, "", "")))
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // POSTで登録（マルチ）
  @Test
  fun testAttendee_7() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/attendee/multi") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(
          Gson().toJson(
            arrayOf(
              Attendee(1, 1, "test", "sample.inc"),
              Attendee(2, 1, "hoge", "hoge.inc"),
              Attendee(3, 1, "fuga", "fuga.inc")
            )
          )
        )
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val attendee = Gson().fromJson(this, Array<Attendee>::class.java)
          assertEquals(3, attendee.count())
          assertEquals(1, attendee[0].attendee_id)
          assertEquals(1, attendee[0].minutes_id)
          assertEquals("test", attendee[0].attendee_name)
          assertEquals("sample.inc", attendee[0].organization)
          assertEquals(2, attendee[1].attendee_id)
          assertEquals(1, attendee[1].minutes_id)
          assertEquals("hoge", attendee[1].attendee_name)
          assertEquals("hoge.inc", attendee[1].organization)
          assertEquals(3, attendee[2].attendee_id)
          assertEquals(1, attendee[2].minutes_id)
          assertEquals("fuga", attendee[2].attendee_name)
          assertEquals("fuga.inc", attendee[2].organization)
        }
      }
    }
  }

  // POSTで登録（マルチ）：異常系
  @Test
  fun testAttendee_8() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/attendee/multi") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(
          Gson().toJson(
            arrayOf(
              Attendee(1, 1, "test", "sample.inc"),
              Attendee(2, 1, "", ""),
              Attendee(3, 1, "fuga", "fuga.inc")
            )
          )
        )
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // POSTで登録（マルチ）：異常系
  @Test
  fun testAttendee_9() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/attendee/multi") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(
          Gson().toJson(
            arrayOf<Attendee>()
          )
        )
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // PUTで更新
  @Test
  fun testAttendee_10() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Put, "/api/attendee") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(Attendee(1, 1, "sample", "sample.inc")))
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val attendee = Gson().fromJson(this, Attendee::class.java)
          assertEquals(1, attendee.attendee_id)
          assertEquals(1, attendee.minutes_id)
          assertEquals("sample", attendee.attendee_name)
          assertEquals("sample.inc", attendee.organization)
        }
      }
    }
  }

  // PUTで更新：異常系
  @Test
  fun testAttendee_11() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Put, "/api/attendee") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(Attendee(1, 1, "", "")))
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // DELETEで削除
  @Test
  fun testAttendee_12() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Delete, "/api/attendee/1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
      }
    }
  }

  // DELETEで削除：異常系
  @Test
  fun testAttendee_13() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Delete, "/api/attendee/hoge"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }
}
