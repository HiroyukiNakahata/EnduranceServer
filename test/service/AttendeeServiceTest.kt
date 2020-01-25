package service

import com.endurance.injector.Injector
import com.endurance.model.Attendee
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
import java.sql.SQLException

class AttendeeServiceTest {

  private val databaseTester = JdbcDatabaseTester(
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost/endurance",
    "postgres",
    "1203"
  )

  private val beforeData = "./test/data/attendee_test/AttendeeTestDataBefore.xml"
  private val afterData1 = "./test/data/attendee_test/AttendeeTestDataAfter_1.xml"
  private val afterData2 = "./test/data/attendee_test/AttendeeTestDataAfter_2.xml"
  private val afterData3 = "./test/data/attendee_test/AttendeeTestDataAfter_3.xml"
  private val afterData4 = "./test/data/attendee_test/AttendeeTestDataAfter_4.xml"
  private val afterData5 = "./test/data/attendee_test/AttendeeTestDataAfter_5.xml"

  private val attendeeService = Injector.getAttendeeService()

  @Before
  fun setUp() {
    val dataset: IDataSet = FlatXmlDataSetBuilder().build(File(beforeData)) as IDataSet
    databaseTester.dataSet = dataset
    databaseTester.onSetup()
  }

  @Test
  fun find() {
    val actual = attendeeService.find()
    val expected = Attendee(
      attendee_id = 1,
      minutes_id = 1,
      attendee_name = "nakahata",
      organization = "gumi"
    )
    assertThat(actual[0].attendee_id, `is`(expected.attendee_id))
    assertThat(actual[0].minutes_id, `is`(expected.minutes_id))
    assertThat(actual[0].attendee_name, `is`(expected.attendee_name))
    assertThat(actual[0].organization, `is`(expected.organization))
  }

  @Test
  fun testFind() {
    val actual = attendeeService.find(1)
    val expected = Attendee(
      attendee_id = 1,
      minutes_id = 1,
      attendee_name = "nakahata",
      organization = "gumi"
    )
    assertThat(actual.attendee_id, `is`(expected.attendee_id))
    assertThat(actual.minutes_id, `is`(expected.minutes_id))
    assertThat(actual.attendee_name, `is`(expected.attendee_name))
    assertThat(actual.organization, `is`(expected.organization))
  }

  @Test
  fun insert() {
    val attendee = Attendee(
      attendee_id = 3,
      minutes_id = 1,
      attendee_name = "hogefuga",
      organization = "fumofumo.inc"
    )

    attendeeService.insert(attendee)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData1))
    var expectedTable = expectedDataset.getTable("attendee")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("attendee_id")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("attendee")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("attendee_id")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }

  // 複数インサート
  @Test
  fun insertMulti() {
    val attendees = listOf(
      Attendee(
        attendee_id = 3,
        minutes_id = 1,
        attendee_name = "hogefuga",
        organization = "fumofumo.inc"
      ),
      Attendee(
        attendee_id = 4,
        minutes_id = 1,
        attendee_name = "yamada",
        organization = "yamada.inc"
      ),
      Attendee(
        attendee_id = 5,
        minutes_id = 1,
        attendee_name = "goro",
        organization = "goro.inc"
      )
    )

    attendeeService.insertMulti(attendees)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData4))
    var expectedTable = expectedDataset.getTable("attendee")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("attendee_id")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("attendee")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("attendee_id")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }

  // 複数インサート：異常系
  @Test
  fun insertMulti_2() {
    val attendees = listOf(
      Attendee(
        attendee_id = 3,
        minutes_id = 1,
        attendee_name = "hogefuga",
        organization = "fumofumo.inc"
      ),
      Attendee(
        attendee_id = 4,
        minutes_id = 1,
        attendee_name = "yamada",
        organization = "yamada.inc"
      ),
      Attendee(
        attendee_id = 5,
        minutes_id = 3,
        attendee_name = "goro",
        organization = "goro.inc"
      )
    )

    try {
      attendeeService.insertMulti(attendees)
    } catch (e: SQLException) {
      println(e.message)
    }

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData5))
    var expectedTable = expectedDataset.getTable("attendee")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("attendee_id")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("attendee")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("attendee_id")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }

  @Test
  fun update() {
    val attendee = Attendee(
      attendee_id = 1,
      minutes_id = 1,
      attendee_name = "update",
      organization = "update.inc"
    )

    attendeeService.update(attendee)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData2))
    val expectedTable = expectedDataset.getTable("attendee")

    val databaseDataset = databaseTester.connection.createDataSet()
    val actualTable = databaseDataset.getTable("attendee")

    Assertion.assertEquals(expectedTable, actualTable)
  }

  @Test
  fun delete() {
    attendeeService.delete(1)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData3))
    val expectedTable = expectedDataset.getTable("attendee")

    val databaseDataset = databaseTester.connection.createDataSet()
    val actualTable = databaseDataset.getTable("attendee")

    Assertion.assertEquals(expectedTable, actualTable)
  }
}
