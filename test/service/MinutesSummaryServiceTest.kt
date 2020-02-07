package service

import com.endurance.model.MinutesSummary
import com.endurance.service.HikariService
import com.endurance.service.MinutesSummaryService
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

class MinutesSummaryServiceTest {

  private val beforeData = "./testresources/data/minutes_summary/MinutesSummaryTestDataBefore.xml"

  private val minutesSummaryService = MinutesSummaryService()

  companion object {
    private lateinit var original: File
    private val databaseTester =  HikariService.readConfig().run {
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
    val actual = minutesSummaryService.find()
    val expected = listOf(
      MinutesSummary(
        1,
        "Hiroyuki Nakahata",
        "リーマン幾何学",
        "リーマン研究所",
        "Ebisu",
        "リーマン幾何とその応用",
        "興味深い知見",
        "2020-01-29 00:00:00+09"
      ),
      MinutesSummary(
        2,
        "David Hilbert",
        "複素多様体",
        "複素研究所",
        "Shibuya",
        "複素多様体の応用分野",
        "エレガントな証明とその応用",
        "2020-01-29 00:00:00+09"
      ),
      MinutesSummary(
        3,
        "Hiroyuki Nakahata",
        "代数的整数論",
        "代数学プログラム",
        "Yoyogi",
        "代数的整数論の発展",
        "類対論の進展",
        "2020-01-29 00:00:00+09"
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
    }
  }

  @Test
  fun testFind() {
    val actual = minutesSummaryService.find(2, 1)
    val expected = listOf(
      MinutesSummary(
        2,
        "David Hilbert",
        "複素多様体",
        "複素研究所",
        "Shibuya",
        "複素多様体の応用分野",
        "エレガントな証明とその応用",
        "2020-01-29 00:00:00+09"
      ),
      MinutesSummary(
        3,
        "Hiroyuki Nakahata",
        "代数的整数論",
        "代数学プログラム",
        "Yoyogi",
        "代数的整数論の発展",
        "類対論の進展",
        "2020-01-29 00:00:00+09"
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
    }
  }

  @Test
  fun findByUser() {
    val actual = minutesSummaryService.findByUser(2)
    val expected = listOf(
      MinutesSummary(
        2,
        "David Hilbert",
        "複素多様体",
        "複素研究所",
        "Shibuya",
        "複素多様体の応用分野",
        "エレガントな証明とその応用",
        "2020-01-29 00:00:00+09"
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
    }
  }

  @Test
  fun testFindByUser() {
    val actual = minutesSummaryService.findByUser(1, 1, 1)
    val expected = listOf(
      MinutesSummary(
        3,
        "Hiroyuki Nakahata",
        "代数的整数論",
        "代数学プログラム",
        "Yoyogi",
        "代数的整数論の発展",
        "類対論の進展",
        "2020-01-29 00:00:00+09"
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
    }
  }
}
