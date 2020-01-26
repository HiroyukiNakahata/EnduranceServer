package service

import com.endurance.model.Attendee
import com.endurance.service.AttendeeService
import com.endurance.service.HikariService
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
import java.sql.SQLException
import kotlin.test.assertEquals

class AttendeeServiceTest {

  private val beforeData = "./testresources/data/attendee_test/AttendeeTestDataBefore.xml"
  private val afterData1 = "./testresources/data/attendee_test/AttendeeTestDataAfter_1.xml"
  private val afterData2 = "./testresources/data/attendee_test/AttendeeTestDataAfter_2.xml"
  private val afterData3 = "./testresources/data/attendee_test/AttendeeTestDataAfter_3.xml"
  private val afterData4 = "./testresources/data/attendee_test/AttendeeTestDataAfter_4.xml"
  private val afterData5 = "./testresources/data/attendee_test/AttendeeTestDataAfter_5.xml"

  private val attendeeService = AttendeeService()

  companion object {
    private lateinit var original: File
    private val databaseConfig = HikariService.readConfig()
    private val databaseTester = databaseConfig.run {
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

    assertEquals(expected.attendee_id, actual[0].attendee_id)
    assertEquals(expected.minutes_id, actual[0].minutes_id)
    assertEquals(expected.attendee_name, actual[0].attendee_name)
    assertEquals(expected.organization, actual[0].organization)
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

    assertEquals(expected.attendee_id, actual.attendee_id)
    assertEquals(expected.minutes_id, actual.minutes_id)
    assertEquals(expected.attendee_name, actual.attendee_name)
    assertEquals(expected.organization, actual.organization)
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
      actualTable, arrayOf("attendee_id")
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
      actualTable, arrayOf("attendee_id")
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
      actualTable, arrayOf("attendee_id")
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
