package com.endurance.routing

import com.endurance.model.UserLogin
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

class LoginRoutingTest {

  data class TokenAndId(val token: String, val id: Int)

  // POSTでログイン
  @Test
  fun testLogin_1() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/login") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(UserLogin("test-sample@gumi.co.jp", "gumi9393")))
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val tokenAndId = Gson().fromJson(this, TokenAndId::class.java)
          assertEquals(1, tokenAndId.id)
        }
      }
    }
  }

  // POSTでログイン失敗
  @Test
  fun testLogin_2() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/login") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(UserLogin("test-sample@gumi.co.jp", "fugahoge")))
      }.apply {
        assertEquals(HttpStatusCode.Unauthorized, response.status())
      }
    }
  }
}
