package com.endurance

import com.endurance.injector.Injector
import com.endurance.model.User
import com.google.gson.Gson
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*


class ApplicationTest {
  @BeforeTest
  fun before() {
    Injector.testing = true
  }

  @Test
  fun testRoot() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        assertEquals("OK", response.content)
      }
    }
  }
}
