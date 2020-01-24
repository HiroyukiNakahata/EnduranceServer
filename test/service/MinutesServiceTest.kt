package service

import com.endurance.injector.Injector
import com.endurance.model.Minutes
import org.dbunit.Assertion
import org.dbunit.JdbcDatabaseTester
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.filter.DefaultColumnFilter
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.File

class MinutesServiceTest {

  private val databaseTester = JdbcDatabaseTester(
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost/endurance",
    "postgres",
    "1203"
  )

  private val beforeData = "./test/data/minutes_test/MinutesTestDataBefore.xml"
  private val afterData1 = "./test/data/minutes_test/MinutesTestDataAfter_1.xml"
  private val afterData2 = "./test/data/minutes_test/MinutesTestDataAfter_2.xml"
  private val afterData3 = "./test/data/minutes_test/MinutesTestDataAfter_3.xml"

  private val minutesService = Injector.getMinutesService()

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

    assertThat(actual[0].minutes_id, `is`(expected.minutes_id))
    assertThat(actual[0].user_id, `is`(expected.user_id))
    assertThat(actual[0].project_id, `is`(expected.project_id))
    assertThat(actual[0].place, `is`(expected.place))
    assertThat(actual[0].theme, `is`(expected.theme))
    assertThat(actual[0].summary, `is`(expected.summary))
    assertThat(actual[0].body_text, `is`(expected.body_text))
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

    assertThat(actual.minutes_id, `is`(expected.minutes_id))
    assertThat(actual.user_id, `is`(expected.user_id))
    assertThat(actual.project_id, `is`(expected.project_id))
    assertThat(actual.place, `is`(expected.place))
    assertThat(actual.theme, `is`(expected.theme))
    assertThat(actual.summary, `is`(expected.summary))
    assertThat(actual.body_text, `is`(expected.body_text))
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
      expectedTable, arrayOf("minutes_id", "time_stamp")
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

    minutesService.insert(minutes)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData2))
    var expectedTable = expectedDataset.getTable("minutes")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("time_stamp")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("minutes")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("time_stamp")
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
      expectedTable, arrayOf("time_stamp")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }
}
