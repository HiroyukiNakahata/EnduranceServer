package service

import com.endurance.model.Minutes
import com.endurance.service.HikariService
import com.endurance.service.MinutesService
import com.endurance.service.restoreOriginalData
import com.endurance.service.saveOriginalData
import org.dbunit.Assertion
import org.dbunit.JdbcDatabaseTester
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.filter.DefaultColumnFilter
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class MinutesServiceTest {

  private val beforeData = "./testresources/data/minutes/MinutesTestDataBefore.xml"
  private val afterData1 = "./testresources/data/minutes/MinutesTestDataAfter_1.xml"
  private val afterData2 = "./testresources/data/minutes/MinutesTestDataAfter_2.xml"
  private val afterData3 = "./testresources/data/minutes/MinutesTestDataAfter_3.xml"

  private val minutesService = MinutesService()

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
    val actual = minutesService.find()
    val expected = listOf(
      Minutes(
        minutes_id = 1,
        user_id = 1,
        project_id = 1,
        place = "Ebisu",
        theme = "おひるごはん",
        summary = "おひるごはん",
        body_text = "おひるごはん",
        time_stamp = "2020-01-23 12:14:47"
      ),
      Minutes(
        minutes_id = 2,
        user_id = 1,
        project_id = 1,
        place = "Shinagawa",
        theme = "ばんごはん",
        summary = "ばんごはん",
        body_text = "ばんごはん",
        time_stamp = "2020-01-23 12:14:47"
      )
    )

    assertEquals(expected.count(), actual.count())
    expected.zip(actual).forEach { pair ->
      assertEquals(pair.first.minutes_id, pair.second.minutes_id)
      assertEquals(pair.first.user_id, pair.second.user_id)
      assertEquals(pair.first.project_id, pair.second.project_id)
      assertEquals(pair.first.place, pair.second.place)
      assertEquals(pair.first.theme, pair.second.theme)
      assertEquals(pair.first.summary, pair.second.summary)
      assertEquals(pair.first.body_text, pair.second.body_text)
    }
  }

  @Test
  fun testFind() {
    val actual = minutesService.find(1)
    val expected = Minutes(
      minutes_id = 1,
      user_id = 1,
      project_id = 1,
      place = "Ebisu",
      theme = "おひるごはん",
      summary = "おひるごはん",
      body_text = "おひるごはん",
      time_stamp = "2020-01-23 12:14:47"
    )

    assertEquals(expected.minutes_id, actual.minutes_id)
    assertEquals(expected.user_id, actual.user_id)
    assertEquals(expected.project_id, actual.project_id)
    assertEquals(expected.place, actual.place)
    assertEquals(expected.theme, actual.theme)
    assertEquals(expected.summary, actual.summary)
    assertEquals(expected.body_text, actual.body_text)
  }

  @Test
  fun insert() {
    val minutes = Minutes(
      minutes_id = 3,
      user_id = 1,
      project_id = 1,
      place = "Yoyogi",
      theme = "あさごはん",
      summary = "あさごはん",
      body_text = "あさごはん",
      time_stamp = "2020-01-23 12:14:47"
    )

    minutesService.insert(minutes)

    val expected = FlatXmlDataSetBuilder().build(File(afterData1))
      .getTable("minutes")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("minutes_id", "time_stamp"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("minutes")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("minutes_id", "time_stamp"))
      }

    Assertion.assertEquals(expected, actual)
  }

  @Test
  fun update() {
    val minutes = Minutes(
      minutes_id = 1,
      user_id = 1,
      project_id = 1,
      place = "Nakano",
      theme = "おやつ",
      summary = "おやつ",
      body_text = "おやつ",
      time_stamp = "2020-01-23 12:14:47"
    )

    minutesService.update(minutes)

    val expected = FlatXmlDataSetBuilder().build(File(afterData2))
      .getTable("minutes")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("time_stamp"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("minutes")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("time_stamp"))
      }

    Assertion.assertEquals(expected, actual)
  }

  @Test
  fun delete() {
    minutesService.delete(1)

    val expected = FlatXmlDataSetBuilder().build(File(afterData3))
      .getTable("minutes")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("time_stamp"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("minutes")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("time_stamp"))
      }

    Assertion.assertEquals(expected, actual)
  }
}
