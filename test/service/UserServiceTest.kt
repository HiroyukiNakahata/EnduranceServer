package service

import com.endurance.model.User
import com.endurance.service.HikariService
import com.endurance.service.UserService
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

class UserServiceTest {

  private val beforeData = "./testresources/data/user_test/UserTestDataBefore.xml"
  private val afterData1 = "./testresources/data/user_test/UserTestDataAfter_1.xml"
  private val afterData2 = "./testresources/data/user_test/UserTestDataAfter_2.xml"
  private val afterData3 = "./testresources/data/user_test/UserTestDataAfter_3.xml"

  private val userService = UserService()

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
    val actual = userService.find()
    val expected = User(
      user_id = 1,
      first_name = "hiroyuki",
      last_name = "nakahata",
      mail_address = "nakahata@gumi.co.jp"
    )

    assertEquals(expected.user_id, actual[0].user_id)
    assertEquals(expected.first_name, actual[0].first_name)
    assertEquals(expected.last_name, actual[0].last_name)
    assertEquals(expected.mail_address, actual[0].mail_address)
  }

  @Test
  fun testFind() {
    val actual = userService.find(1)
    val expected = User(
      user_id = 1,
      first_name = "hiroyuki",
      last_name = "nakahata",
      mail_address = "nakahata@gumi.co.jp"
    )

    assertEquals(expected.user_id, actual.user_id)
    assertEquals(expected.first_name, actual.first_name)
    assertEquals(expected.last_name, actual.last_name)
    assertEquals(expected.mail_address, actual.mail_address)
  }

  @Test
  fun insert() {
    val user = User(
      user_id = 2,
      first_name = "hoge",
      last_name = "fuga",
      mail_address = "hogehoge@gumi.co.jp"
    )

    userService.insert(user)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData1))
    var expectedTable = expectedDataset.getTable("users")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("user_id")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("users")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      actualTable, arrayOf("user_id")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }

  @Test
  fun update() {
    val user = User(
      user_id = 1,
      first_name = "test",
      last_name = "sample",
      mail_address = "sample@gumi.co.jp"
    )

    userService.update(user)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData2))
    val expectedTable = expectedDataset.getTable("users")

    val databaseDataset = databaseTester.connection.createDataSet()
    val actualTable = databaseDataset.getTable("users")

    Assertion.assertEquals(expectedTable, actualTable)
  }

  @Test
  fun delete() {
    userService.delete(1)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData3))
    val expectedTable = expectedDataset.getTable("users")

    val databaseDataset = databaseTester.connection.createDataSet()
    val actualTable = databaseDataset.getTable("users")

    Assertion.assertEquals(expectedTable, actualTable)
  }
}
