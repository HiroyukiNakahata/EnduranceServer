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

  private val beforeData = "./testresources/data/user/UserTestDataBefore.xml"
  private val afterData1 = "./testresources/data/user/UserTestDataAfter_1.xml"
  private val afterData2 = "./testresources/data/user/UserTestDataAfter_2.xml"
  private val afterData3 = "./testresources/data/user/UserTestDataAfter_3.xml"

  private val userService = UserService()

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
    val actual = userService.find()
    val expected = listOf(
      User(
        user_id = 1,
        first_name = "hiroyuki",
        last_name = "nakahata",
        mail_address = "nakahata@gumi.co.jp"
      ),
      User(
        user_id = 2,
        first_name = "fizz",
        last_name = "buzz",
        mail_address = "fizzbuzz@gumi.co.jp"
      )
    )

    assertEquals(expected.count(), actual.count())
    expected.zip(actual).forEach { pair ->
      assertEquals(pair.first, pair.second)
    }
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

    assertEquals(expected, actual)
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

    val expected = FlatXmlDataSetBuilder().build(File(afterData1))
      .getTable("users")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("user_id"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("users")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("user_id"))
      }

    Assertion.assertEquals(expected, actual)
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

    val expected = FlatXmlDataSetBuilder().build(File(afterData2))
      .getTable("users")

    val actual = databaseTester.connection.createDataSet()
      .getTable("users")

    Assertion.assertEquals(expected, actual)
  }

  @Test
  fun delete() {
    userService.delete(1)

    val expected = FlatXmlDataSetBuilder().build(File(afterData3))
      .getTable("users")

    val actual = databaseTester.connection.createDataSet()
      .getTable("users")

    Assertion.assertEquals(expected, actual)
  }
}
