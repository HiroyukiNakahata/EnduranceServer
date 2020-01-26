package service

import com.endurance.model.Todo
import com.endurance.service.HikariService
import com.endurance.service.TodoService
import com.endurance.service.restoreOriginalData
import com.endurance.service.saveOriginalData
import org.dbunit.Assertion
import org.dbunit.JdbcDatabaseTester
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.filter.DefaultColumnFilter
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.sql.SQLException

class TodoServiceTest {

  private val beforeData = "./testresources/data/todo_test/TodoTestDataBefore.xml"
  private val afterData1 = "./testresources/data/todo_test/TodoTestDataAfter_1.xml"
  private val afterData2 = "./testresources/data/todo_test/TodoTestDataAfter_2.xml"
  private val afterData3 = "./testresources/data/todo_test/TodoTestDataAfter_3.xml"
  private val afterData4 = "./testresources/data/todo_test/TodoTestDataAfter_4.xml"
  private val afterData5 = "./testresources/data/todo_test/TodoTestDataAfter_5.xml"

  private val todoService = TodoService()

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
    val actual = todoService.find()
    val expected = Todo(
      todo_id = 1,
      minutes_id = 1,
      project_id = 1,
      user_id = 1,
      task_title = "おひるごはん",
      task_body = "おひるごはんを決める",
      start_time_stamp = "2020-01-23 12:14:47.0",
      end_time_stamp = "2020-01-23 12:14:47.0",
      status = false
    )

    assertEquals(expected.todo_id, actual[0].todo_id)
    assertEquals(expected.minutes_id, actual[0].minutes_id)
    assertEquals(expected.project_id, actual[0].project_id)
    assertEquals(expected.user_id, actual[0].user_id)
    assertEquals(expected.task_title, actual[0].task_title)
    assertEquals(expected.task_body, actual[0].task_body)
    assertEquals(expected.status, actual[0].status)
  }

  @Test
  fun testFind() {
    val actual = todoService.find(1)
    val expected = Todo(
      todo_id = 1,
      minutes_id = 1,
      project_id = 1,
      user_id = 1,
      task_title = "おひるごはん",
      task_body = "おひるごはんを決める",
      start_time_stamp = "2020-01-23 12:14:47.0",
      end_time_stamp = "2020-01-23 12:14:47.0",
      status = false
    )

    assertEquals(expected.todo_id, actual.todo_id)
    assertEquals(expected.minutes_id, actual.minutes_id)
    assertEquals(expected.project_id, actual.project_id)
    assertEquals(expected.user_id, actual.user_id)
    assertEquals(expected.task_title, actual.task_title)
    assertEquals(expected.task_body, actual.task_body)
    assertEquals(expected.status, actual.status)
  }

  @Test
  fun insert() {
    val todo = Todo(
      todo_id = 1,
      minutes_id = 1,
      project_id = 1,
      user_id = 1,
      task_title = "あさごはん",
      task_body = "あさごはんを決める",
      start_time_stamp = "2020-01-27T20:14:47Z",
      end_time_stamp = "2020-01-27T21:14:47Z",
      status = false
    )

    todoService.insert(todo)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData1))
    var expectedTable = expectedDataset.getTable("todo")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("todo_id", "start_time_stamp", "end_time_stamp")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("todo")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      actualTable, arrayOf("todo_id", "start_time_stamp", "end_time_stamp")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }

  // 複数インサート
  @Test
  fun insertMulti() {
    val todoList = listOf(
      Todo(
        todo_id = 1,
        minutes_id = 1,
        project_id = 1,
        user_id = 1,
        task_title = "あさごはん",
        task_body = "あさごはんを決める",
        start_time_stamp = "2020-01-27T20:14:47Z",
        end_time_stamp = "2020-01-27T21:14:47Z",
        status = false
      ),
      Todo(
        todo_id = 1,
        minutes_id = 1,
        project_id = 1,
        user_id = 1,
        task_title = "おやつ",
        task_body = "おやつを決める",
        start_time_stamp = "2020-01-27T20:14:47Z",
        end_time_stamp = "2020-01-27T21:14:47Z",
        status = false
      )
    )

    todoService.insertMulti(todoList)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData2))
    var expectedTable = expectedDataset.getTable("todo")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("todo_id", "start_time_stamp", "end_time_stamp")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("todo")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      actualTable, arrayOf("todo_id", "start_time_stamp", "end_time_stamp")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }

  // 複数インサート：異常系
  @Test
  fun insertMulti_2() {
    val todoList = listOf(
      Todo(
        todo_id = 1,
        minutes_id = 1,
        project_id = 1,
        user_id = 1,
        task_title = "あさごはん",
        task_body = "あさごはんを決める",
        start_time_stamp = "2020-01-27T20:14:47Z",
        end_time_stamp = "2020-01-27T21:14:47Z",
        status = false
      ),
      Todo(
        todo_id = 1,
        minutes_id = 4,
        project_id = 5,
        user_id = 6,
        task_title = "おやつ",
        task_body = "おやつを決める",
        start_time_stamp = "2020-01-27T20:14:47Z",
        end_time_stamp = "2020-01-27T21:14:47Z",
        status = false
      )
    )

    try {
      todoService.insertMulti(todoList)
    } catch (e: SQLException) {
      println(e.message)
    }

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData3))
    var expectedTable = expectedDataset.getTable("todo")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("todo_id", "start_time_stamp", "end_time_stamp")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("todo")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      actualTable, arrayOf("todo_id", "start_time_stamp", "end_time_stamp")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }

  @Test
  fun update() {
    val todo = Todo(
      todo_id = 1,
      minutes_id = 1,
      project_id = 1,
      user_id = 1,
      task_title = "午後の紅茶",
      task_body = "お茶会の日程を決める",
      start_time_stamp = "2020-01-27T20:14:47Z",
      end_time_stamp = "2020-01-27T21:14:47Z",
      status = false
    )

    todoService.update(todo)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData4))
    var expectedTable = expectedDataset.getTable("todo")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("todo_id", "start_time_stamp", "end_time_stamp")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("todo")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      actualTable, arrayOf("todo_id", "start_time_stamp", "end_time_stamp")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }

  @Test
  fun delete() {
    todoService.delete(1)

    val expectedDataset = FlatXmlDataSetBuilder().build(File(afterData5))
    var expectedTable = expectedDataset.getTable("todo")
    expectedTable = DefaultColumnFilter.excludedColumnsTable(
      expectedTable, arrayOf("todo_id", "start_time_stamp", "end_time_stamp")
    )

    val databaseDataset = databaseTester.connection.createDataSet()
    var actualTable = databaseDataset.getTable("todo")
    actualTable = DefaultColumnFilter.excludedColumnsTable(
      actualTable, arrayOf("todo_id", "start_time_stamp", "end_time_stamp")
    )

    Assertion.assertEquals(expectedTable, actualTable)
  }
}
