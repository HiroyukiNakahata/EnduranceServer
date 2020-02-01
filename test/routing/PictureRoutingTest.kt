package com.endurance.routing

import com.endurance.authentication.JwtAuth
import com.endurance.model.Picture
import com.endurance.module
import com.google.gson.Gson
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PictureRoutingTest {

  private var token = ""

  @BeforeTest
  fun beforeTest() {
    token = JwtAuth.createToken(1, System.currentTimeMillis())
  }

  // GETで全件取得
  @Test
  fun testPicture_1() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/picture"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val picture = Gson().fromJson(this, Array<Picture>::class.java)
          assertEquals(1, picture.count())
          assertEquals(1, picture[0].picture_id)
          assertEquals(1, picture[0].minutes_id)
          assertEquals("image.jpg", picture[0].picture_path)
        }
      }
    }
  }

  // GETでID指定
  @Test
  fun testPicture_2() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/picture/1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val picture = Gson().fromJson(this, Picture::class.java)
          assertEquals(1, picture.picture_id)
          assertEquals(1, picture.minutes_id)
          assertEquals("image.jpg", picture.picture_path)
        }
      }
    }
  }

  // GETでID指定：異常系
  @Test
  fun testPicture_3() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/picture/3"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.NotFound, response.status())
      }
    }
  }

  // GETでID指定：異常系
  @Test
  fun testPicture_4() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/picture/hoge"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // TODO: 残りのテストを書く
}
