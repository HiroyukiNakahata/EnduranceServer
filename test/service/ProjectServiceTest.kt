package service

import com.endurance.model.Project
import com.endurance.service.HikariService
import com.endurance.service.ProjectService
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

class ProjectServiceTest {

  private val beforeData = "./testresources/data/project/ProjectTestDataBefore.xml"
  private val afterData1 = "./testresources/data/project/ProjectTestDataAfter_1.xml"
  private val afterData2 = "./testresources/data/project/ProjectTestDataAfter_2.xml"
  private val afterData3 = "./testresources/data/project/ProjectTestDataAfter_3.xml"

  private val projectService = ProjectService()

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
    val actual = projectService.find()
    val expected = listOf(
      Project(
        1,
        "おひるごはん",
        "Shibuya"
      ),
      Project(
        2,
        "ばんごはん",
        "Yoyogi"
      )
    )

    assertEquals(expected.count(), actual.count())
    expected.zip(actual).forEach { pair ->
      assertEquals(pair.first, pair.second)
    }
  }

  @Test
  fun testFind() {
    val actual = projectService.find(1)
    val expected = Project(
      1,
      "おひるごはん",
      "Shibuya"
    )

    assertEquals(expected, actual)
  }

  @Test
  fun insert() {
    val project = Project(
      3,
      "おやつ",
      "Shinagawa"
    )

    projectService.insert(project)

    val expected = FlatXmlDataSetBuilder().build(File(afterData1))
      .getTable("project")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("project_id"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("project")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("project_id"))
      }

    Assertion.assertEquals(expected, actual)
  }

  @Test
  fun update() {
    val project = Project(
      1,
      "おやつ",
      "Shinagawa"
    )

    projectService.update(project)

    val expected = FlatXmlDataSetBuilder().build(File(afterData2))
      .getTable("project")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("project_id"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("project")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("project_id"))
      }

    Assertion.assertEquals(expected, actual)
  }

  @Test
  fun delete() {
    projectService.delete(1)

    val expected = FlatXmlDataSetBuilder().build(File(afterData3))
      .getTable("project")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("project_id"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("project")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("project_id"))
      }

    Assertion.assertEquals(expected, actual)
  }
}
