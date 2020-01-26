package service

import com.endurance.model.Minutes
import com.endurance.service.HikariService
import com.endurance.service.MinutesService
import org.dbunit.Assertion
import org.dbunit.JdbcDatabaseTester
import org.dbunit.database.QueryDataSet
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.filter.DefaultColumnFilter
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.operation.DatabaseOperation
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.test.assertEquals

class MinutesServiceTest {

  private val beforeData = "./testresources/data/minutes_test/MinutesTestDataBefore.xml"
  private val afterData1 = "./testresources/data/minutes_test/MinutesTestDataAfter_1.xml"
  private val afterData2 = "./testresources/data/minutes_test/MinutesTestDataAfter_2.xml"
  private val afterData3 = "./testresources/data/minutes_test/MinutesTestDataAfter_3.xml"

  private val minutesService = MinutesService()

  companion object {
    private lateinit var original: File
    private val databaseConfig = HikariService.readConfig()
    private val databaseTester = databaseConfig.run {
      JdbcDatabaseTester(driverClass, jdbcUrl, username, password)
    }

    @BeforeClass
    @JvmStatic
    fun beforeTest() {
      val originDataSet = QueryDataSet(databaseTester.connection)
      originDataSet.apply {
        addTable("users")
        addTable("project")
        addTable("minutes")
        addTable("attendee")
        addTable("picture")
        addTable("todo")
      }
      original = File.createTempFile("tmp", ".xml", File("./testresources/data/tmp/"))
      FileOutputStream(original).use {
        FlatXmlDataSet.write(originDataSet, it)
      }
    }

    @AfterClass
    @JvmStatic
    fun afterTest() {
      FileInputStream(original).use {
        val originalDataSet = FlatXmlDataSetBuilder().build(it)
        DatabaseOperation.CLEAN_INSERT.execute(databaseTester.connection, originalDataSet)
      }
    }
  }

  @Before
  fun setUp() {
    val dataset: IDataSet = FlatXmlDataSetBuilder().build(File(beforeData)) as IDataSet
    databaseTester.dataSet = dataset
    databaseTester.onSetup()
  }

  @Test
  fun find() {
    val actual = minutesService.find()
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

    assertEquals(expected.minutes_id, actual[0].minutes_id)
    assertEquals(expected.user_id, actual[0].user_id)
    assertEquals(expected.project_id, actual[0].project_id)
    assertEquals(expected.place, actual[0].place)
    assertEquals(expected.theme, actual[0].theme)
    assertEquals(expected.summary, actual[0].summary)
    assertEquals(expected.body_text, actual[0].body_text)
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

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData1))
    var expectedTable = expectedDataset.getTable("minutes")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("minutes_id", "time_stamp")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("minutes")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      actualTable, arrayOf("minutes_id", "time_stamp")
    )

    Assertion.assertEquals(expectedTable, actualTable)
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

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData2))
    var expectedTable = expectedDataset.getTable("minutes")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("time_stamp")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("minutes")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      actualTable, arrayOf("time_stamp")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }

  @Test
  fun delete() {
    minutesService.delete(1)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData3))
    var expectedTable = expectedDataset.getTable("minutes")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("time_stamp")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("minutes")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      actualTable, arrayOf("time_stamp")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }
}
