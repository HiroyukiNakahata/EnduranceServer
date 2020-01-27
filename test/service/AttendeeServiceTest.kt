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

  private val beforeData = "./testresources/data/attendee/AttendeeTestDataBefore.xml"
  private val afterData1 = "./testresources/data/attendee/AttendeeTestDataAfter_1.xml"
  private val afterData2 = "./testresources/data/attendee/AttendeeTestDataAfter_2.xml"
  private val afterData3 = "./testresources/data/attendee/AttendeeTestDataAfter_3.xml"
  private val afterData4 = "./testresources/data/attendee/AttendeeTestDataAfter_4.xml"
  private val afterData5 = "./testresources/data/attendee/AttendeeTestDataAfter_5.xml"

  private val attendeeService = AttendeeService()

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
    val actual = attendeeService.find()
    val expected = listOf(
      Attendee(
        attendee_id = 1,
        minutes_id = 1,
        attendee_name = "nakahata",
        organization = "gumi"
      ),
      Attendee(
        attendee_id = 2,
        minutes_id = 1,
        attendee_name = "hoge",
        organization = "gumi"
      )
    )

    assertEquals(expected.count(), actual.count())
    expected.zip(actual).forEach { pair ->
      assertEquals(pair.first, pair.second)
    }
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

    assertEquals(expected, actual)
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

    val expected = FlatXmlDataSetBuilder().build(File(afterData1))
      .getTable("attendee")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("attendee_id"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("attendee")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("attendee_id"))
      }

    Assertion.assertEquals(expected, actual)
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

    val expected = FlatXmlDataSetBuilder().build(File(afterData4))
      .getTable("attendee")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("attendee_id"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("attendee")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("attendee_id"))
      }

    Assertion.assertEquals(expected, actual)
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

    val expected = FlatXmlDataSetBuilder().build(File(afterData5))
      .getTable("attendee")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("attendee_id"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("attendee")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("attendee_id"))
      }

    Assertion.assertEquals(expected, actual)
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

    val expected = FlatXmlDataSetBuilder().build(File(afterData2))
      .getTable("attendee")

    val actual = databaseTester.connection.createDataSet()
      .getTable("attendee")

    Assertion.assertEquals(expected, actual)
  }

  @Test
  fun delete() {
    attendeeService.delete(1)

    val expected = FlatXmlDataSetBuilder().build(File(afterData3))
      .getTable("attendee")

    val actual = databaseTester.connection.createDataSet()
      .getTable("attendee")

    Assertion.assertEquals(expected, actual)
  }
}
