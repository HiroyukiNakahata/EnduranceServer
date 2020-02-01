package com.endurance.routing

import com.endurance.authentication.JwtAuth
import com.endurance.model.Project
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

class ProjectRoutingTest {

  private var token = ""

  @BeforeTest
  fun beforeTest() {
    token = JwtAuth.createToken(1, System.currentTimeMillis())
  }

  // GETで全件取得
  @Test
  fun testProject_1() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/project"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val projects = Gson().fromJson(this, Array<Project>::class.java)
          assertEquals(1, projects.count())
          assertEquals(1, projects[0].project_id)
          assertEquals("test", projects[0].project_name)
          assertEquals("sample", projects[0].client)
        }
      }
    }
  }

  // GETでID指定
  @Test
  fun testProject_2() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/project/1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val project = Gson().fromJson(this, Project::class.java)
          assertEquals(1, project.project_id)
          assertEquals("test", project.project_name)
          assertEquals("sample", project.client)
        }
      }
    }
  }

  // GETでID指定：異常系
  @Test
  fun testProject_3() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/project/3"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.NotFound, response.status())
      }
    }
  }

  // POSTで登録
  @Test
  fun testProject_4() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/project"){
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(Project(1, "test", "sample")))
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val project = Gson().fromJson(this, Project::class.java)
          assertEquals(1, project.project_id)
          assertEquals("test", project.project_name)
          assertEquals("sample", project.client)
        }
      }
    }
  }

  // POSTで登録：異常系
  @Test
  fun testProject_5() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Post, "/api/project"){
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(Project(1, "", "")))
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // PUTで更新
  @Test
  fun testProject_6() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Put, "/api/project"){
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(Project(1, "test", "sample")))
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        response.content?.run {
          val project = Gson().fromJson(this, Project::class.java)
          assertEquals(1, project.project_id)
          assertEquals("test", project.project_name)
          assertEquals("sample", project.client)
        }
      }
    }
  }

  // PUTで更新：異常系
  @Test
  fun testProject_7() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Put, "/api/project"){
        addHeader("Authorization", "Bearer $token")
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Gson().toJson(Project(1, "", "")))
      }.apply {
        assertEquals(HttpStatusCode.BadRequest, response.status())
      }
    }
  }

  // DELETEで削除
  @Test
  fun testProject_8() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Delete, "/api/project/1"){
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
      }
    }
  }
}
