package service

import com.endurance.model.MinutesAll
import com.endurance.service.HikariService
import com.endurance.service.MinutesAllService
import com.endurance.service.restoreOriginalData
import com.endurance.service.saveOriginalData
import org.dbunit.JdbcDatabaseTester
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

class MinutesAllServiceTest {

  private val beforeData = "./testresources/data/minutes_all/MinutesAllTestDataBefore.xml"

  private val minutesAllService = MinutesAllService()

  companion object {
    private lateinit var original: File
    private val databaseTester = HikariService.readConfig().run {
      JdbcDatabaseTester(driverClass, jdbcUrl, username, password)
    }

    @BeforeClass
    @JvmStatic
    fun beforeTest() {
      original = saveOriginalData(databaseTester)
    }

    @AfterClass
    @JvmStatic
    fun afterTest() {
      restoreOriginalData(original, databaseTester)
    }
  }

  @Before
  fun setUp() {
    databaseTester.apply {
      dataSet = FlatXmlDataSetBuilder().build(File(beforeData)) as IDataSet
      onSetup()
    }
  }

  @Test
  fun find() {
    val actual = minutesAllService.find()
    val expected = listOf(
      MinutesAll(
        1,
        "hiroyuki nakahata",
        "おひるごはん",
        "Shibuya",
        "Ebisu",
        "おひるごはん",
        "おひるごはんの内容について",
        "おひるごはんは何食べる",
        listOf("sample.jpg", "image.png"),
        listOf("nakahata", "yoyogi"),
        listOf("gumi", "shinagawa"),
        "2020-01-23 12:14:47+09"
      ),
      MinutesAll(
        2,
        "hiroyuki nakahata",
        "おひるごはん",
        "Shibuya",
        "Shinagawa",
        "ばんごはん",
        "ばんごはんの内容について",
        "ばんごはんは何食べる",
        listOf(),
        listOf("shibuya"),
        listOf("harajuku"),
        "2020-01-23 12:14:47+09"
      )
    )

    assertEquals(expected.count(), actual.count())

    expected.zip(actual).forEach { pair ->
      assertEquals(pair.first.minutes_id, pair.second.minutes_id)
      assertEquals(pair.first.user_name, pair.second.user_name)
      assertEquals(pair.first.project_name, pair.second.project_name)
      assertEquals(pair.first.client, pair.second.client)
      assertEquals(pair.first.place, pair.second.place)
      assertEquals(pair.first.theme, pair.second.theme)
      assertEquals(pair.first.summary, pair.second.summary)
      assertEquals(pair.first.body_text, pair.second.body_text)
      assertEquals(pair.first.picture_path, pair.second.picture_path)
      assertEquals(pair.first.attendee_name, pair.second.attendee_name)
      assertEquals(pair.first.attendee_organization, pair.second.attendee_organization)
    }
  }

  @Test
  fun testFind() {
    val actual = minutesAllService.find(1)
    val expected = MinutesAll(
      1,
      "hiroyuki nakahata",
      "おひるごはん",
      "Shibuya",
      "Ebisu",
      "おひるごはん",
      "おひるごはんの内容について",
      "おひるごはんは何食べる",
      listOf("sample.jpg", "image.png"),
      listOf("nakahata", "yoyogi"),
      listOf("gumi", "shinagawa"),
      "2020-01-23 12:14:47+09"
    )

    assertEquals(expected.minutes_id, actual.minutes_id)
    assertEquals(expected.user_name, actual.user_name)
    assertEquals(expected.project_name, actual.project_name)
    assertEquals(expected.client, actual.client)
    assertEquals(expected.place, actual.place)
    assertEquals(expected.theme, actual.theme)
    assertEquals(expected.summary, actual.summary)
    assertEquals(expected.body_text, actual.body_text)
    assertEquals(expected.picture_path, actual.picture_path)
    assertEquals(expected.attendee_name, actual.attendee_name)
    assertEquals(expected.attendee_organization, actual.attendee_organization)
  }
}
