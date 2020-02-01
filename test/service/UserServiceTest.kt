package service

import com.endurance.authentication.HashUtil
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
        first_name = "Hiroyuki",
        last_name = "Nakahata",
        mail_address = "nakahata@gumi.co.jp"
      ),
      User(
        user_id = 2,
        first_name = "David",
        last_name = "Hilbert",
        mail_address = "hilbert@gumi.co.jp"
      ),
      User(
        user_id = 3,
        first_name = "Henri",
        last_name = "Poincare",
        mail_address = "poincare@gumi.co.jp"
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
      first_name = "Hiroyuki",
      last_name = "Nakahata",
      mail_address = "nakahata@gumi.co.jp"
    )

    assertEquals(expected, actual)
  }

  @Test
  fun findByMailAddress() {
    val actual = userService.findPasswordByMailAddress("nakahata@gumi.co.jp")
    val expected = Pair(
      "9FD89A274AE758D9D8D98588C367B6C5C77F3C67EF58B26F1AB432EB56EBC0377C80DF2161151A132C69E9039E8DF4B022C28D6C1F0D0FFE66631701993B5582",
      1
    )

    assertEquals(expected, actual)
  }

  @Test
  fun insert() {
    val user = User(
      user_id = 1,
      first_name = "Joseph",
      last_name = "Fourier",
      mail_address = "fourier@gumi.co.jp"
    )

    userService.insert(user, HashUtil.sha512("gumi9393"))

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
      user_id = 2,
      first_name = "George",
      last_name = "Boole",
      mail_address = "boole@gumi.co.jp"
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
    userService.delete(2)

    val expected = FlatXmlDataSetBuilder().build(File(afterData3))
      .getTable("users")

    val actual = databaseTester.connection.createDataSet()
      .getTable("users")

    Assertion.assertEquals(expected, actual)
  }
}
