package com.endurance.routing

import com.endurance.authentication.JwtAuth
import com.endurance.model.Todo
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

class TodoRoutingTest {

  private var token = ""

  @BeforeTest
  fun beforeTest() {
    token = JwtAuth.createToken(1, System.currentTimeMillis())
  }

  // GETで全件取得
  @Test
  fun testTodo_1() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/todo"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val todo = Gson().fromJson(this, Array<Todo>::class.java)
          assertEquals(3, todo.count())
          assertEquals(1, todo[0].project_id)
          assertEquals(1, todo[0].minutes_id)
          assertEquals(1, todo[0].project_id)
          assertEquals(1, todo[0].user_id)
          assertEquals("四次元方程式を解く", todo[0].task_title)
          assertEquals("時空間４次元を表現する数式", todo[0].task_body)
          assertEquals(false, todo[0].status)
        }
      }
    }
  }

  // GETでID指定
  @Test
  fun testTodo_2() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/todo/1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val todo = Gson().fromJson(this, Todo::class.java)
          assertEquals(1, todo.project_id)
          assertEquals(1, todo.minutes_id)
          assertEquals(1, todo.project_id)
          assertEquals(1, todo.user_id)
          assertEquals("四次元方程式を解く", todo.task_title)
          assertEquals("時空間４次元を表現する数式", todo.task_body)
          assertEquals(false, todo.status)
        }
      }
    }
  }

  // GETでID指定：異常系
  @Test
  fun testTodo_3() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/todo/2"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.NotFound, response.status())
      }
    }
  }

  // GETでID指定：異常系
  @Test
  fun testTodo_4() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/todo/hoge"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // POSTで登録
  @Test
  fun testTodo_5() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/todo") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(
          Todo(
            1, 1, 1, 1, "四次元方程式を解く",
            "時空間４次元を表現する数式", "2020-01-29 00:00:00+09",
            "2020-01-29 00:00:00+09", false
          )
        ))
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val todo = Gson().fromJson(this, Todo::class.java)
          assertEquals(1, todo.project_id)
          assertEquals(1, todo.minutes_id)
          assertEquals(1, todo.project_id)
          assertEquals(1, todo.user_id)
          assertEquals("四次元方程式を解く", todo.task_title)
          assertEquals("時空間４次元を表現する数式", todo.task_body)
          assertEquals(false, todo.status)
        }
      }
    }
  }

  // POSTで登録：異常系
  @Test
  fun testTodo_6() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/todo") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(
          Todo(
            1, 1, 1, 1, "",
            "", "2020-01-29 00:00:00+09",
            "2020-01-29 00:00:00+09", false
          )
        ))
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // TODO: マルチインサート実装待ち

  // PUTで更新
  @Test
  fun testTodo_10() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Put, "/api/todo") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(
          Todo(
            1, 1, 1, 1, "四次元方程式を解く",
            "時空間４次元を表現する数式", "2020-01-29 00:00:00+09",
            "2020-01-29 00:00:00+09", false
          )
        ))
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val todo = Gson().fromJson(this, Todo::class.java)
          assertEquals(1, todo.project_id)
          assertEquals(1, todo.minutes_id)
          assertEquals(1, todo.project_id)
          assertEquals(1, todo.user_id)
          assertEquals("四次元方程式を解く", todo.task_title)
          assertEquals("時空間４次元を表現する数式", todo.task_body)
          assertEquals(false, todo.status)
        }
      }
    }
  }

  // PUTで更新：異常系
  @Test
  fun testTodo_11() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Put, "/api/todo") {
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(
          Todo(
            1, 1, 1, 1, "",
            "", "2020-01-29 00:00:00+09",
            "2020-01-29 00:00:00+09", false
          )
        ))
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // DELETEで削除
  @Test
  fun testTodo_12() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Delete, "/api/todo/1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
      }
    }
  }

  // DELETEで削除：異常系
  @Test
  fun testTodo_13() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Delete, "/api/todo/hoge"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }
}
