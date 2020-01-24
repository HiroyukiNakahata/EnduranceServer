package service

import com.endurance.injector.Injector
import com.endurance.model.User
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


class UserServiceTest {

  private val databaseTester = JdbcDatabaseTester(
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost/endurance",
    "postgres",
    "1203"
  )

  private val beforeData = "./test/data/user_test/UserTestDataBefore.xml"
  private val afterData1 = "./test/data/user_test/UserTestDataAfter_1.xml"
  private val afterData2 = "./test/data/user_test/UserTestDataAfter_2.xml"
  private val afterData3 = "./test/data/user_test/UserTestDataAfter_3.xml"

  private val userService = Injector.getUserService()

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
      family_name = "hiroyuki",
      last_name = "nakahata",
      mail_address = "nakahata@gumi.co.jp"
    )
    assertThat(actual[0].user_id, `is`(expected.user_id))
    assertThat(actual[0].family_name, `is`(expected.family_name))
    assertThat(actual[0].last_name, `is`(expected.last_name))
    assertThat(actual[0].mail_address, `is`(expected.mail_address))
  }

  @Test
  fun testFind() {
    val actual = userService.find(1)
    val expected = User(
      user_id = 1,
      family_name = "hiroyuki",
      last_name = "nakahata",
      mail_address = "nakahata@gumi.co.jp"
    )
    assertThat(actual.user_id, `is`(expected.user_id))
    assertThat(actual.family_name, `is`(expected.family_name))
    assertThat(actual.last_name, `is`(expected.last_name))
    assertThat(actual.mail_address, `is`(expected.mail_address))
  }

  @Test
  fun insert() {
    val user = User(
      user_id = 2,
      family_name = "hoge",
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
      expectedTable, arrayOf("user_id")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }

  @Test
  fun update() {
    val user = User(
      user_id = 1,
      family_name = "test",
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
