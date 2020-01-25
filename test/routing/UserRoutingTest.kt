package com.endurance.routing

import com.endurance.injector.Injector
import com.endurance.model.User
import com.endurance.module
import com.google.gson.Gson
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*


class UserRoutingTest {
  @BeforeTest
  fun before() {
    // サービスはスタブを使用
    Injector.testing = true
  }

  // GETで全件取得
  @Test
  fun testUser_1() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/user").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val users = Gson().fromJson(this, Array<User>::class.java)
          assertEquals(1, users.count())
          assertEquals(1, users[0].user_id)
          assertEquals("test", users[0].first_name)
          assertEquals("test", users[0].last_name)
          assertEquals("test@sample.com", users[0].mail_address)
        }
      }
    }
  }

  // GETでID指定
  @Test
  fun testUser_2() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/user/1").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val user = Gson().fromJson(this, User::class.java)
          assertEquals(1, user.user_id)
          assertEquals("test", user.first_name)
          assertEquals("test", user.last_name)
          assertEquals("test@sample.com", user.mail_address)
        }
      }
    }
  }

  // GETでID指定：異常系
  @Test
  fun testUser_3() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/user/2").apply {
        assertEquals(HttpStatusCode.NotFound, response.status())
      }
    }
  }

  // POSTで登録
  @Test
  fun testUser_4() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/user") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(User(1, "test", "test", "test@sample.com")))
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val user = Gson().fromJson(this, User::class.java)
          assertEquals(1, user.user_id)
          assertEquals("test", user.first_name)
          assertEquals("test", user.last_name)
          assertEquals("test@sample.com", user.mail_address)
        }
      }
    }
  }

  // POSTで登録：異常系
  @Test
  fun testUser_5() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/user") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(User(1, "", "", "")))
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // PUTで更新
  @Test
  fun testUser_6() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Put, "/api/user") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(User(1, "test", "test", "test@sample.com")))
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val user = Gson().fromJson(this, User::class.java)
          assertEquals(1, user.user_id)
          assertEquals("test", user.first_name)
          assertEquals("test", user.last_name)
          assertEquals("test@sample.com", user.mail_address)
        }
      }
    }
  }

  // PUTで更新：異常系
  @Test
  fun testUser_7() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Put, "/api/user") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(User(1, "", "", "")))
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // DELETEで削除
  @Test
  fun testUser_8() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Delete, "/api/user/1").apply {
        assertEquals(HttpStatusCode.OK, response.status())
      }
    }
  }
}
