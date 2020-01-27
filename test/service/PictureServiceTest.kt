package service

import com.endurance.model.Picture
import com.endurance.service.HikariService
import com.endurance.service.PictureService
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

class PictureServiceTest {

  private val beforeData = "./testresources/data/picture/PictureTestDataBefore.xml"
  private val afterData1 = "./testresources/data/picture/PictureTestDataAfter_1.xml"
  private val afterData2 = "./testresources/data/picture/PictureTestDataAfter_2.xml"
  private val afterData3 = "./testresources/data/picture/PictureTestDataAfter_3.xml"

  private val pictureService = PictureService()

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
    val actual = pictureService.find()
    val expected = listOf(
      Picture(
        picture_id = 1,
        minutes_id = 1,
        picture_path = "sample.jpg",
        time_stamp = "2020-01-23 12:14:47"
      ),
      Picture(
        picture_id = 2,
        minutes_id = 1,
        picture_path = "image.png",
        time_stamp = "2020-01-23 12:14:47"
      )
    )

    assertEquals(expected.count(), actual.count())
    expected.zip(actual).forEach { pair ->
      assertEquals(pair.first.picture_id, pair.second.picture_id)
      assertEquals(pair.first.minutes_id, pair.second.minutes_id)
      assertEquals(pair.first.picture_path, pair.second.picture_path)
    }
  }

  @Test
  fun testFind() {
    val actual = pictureService.find(1)
    val expected = Picture(
      picture_id = 1,
      minutes_id = 1,
      picture_path = "sample.jpg",
      time_stamp = "2020-01-23 12:14:47"
    )

    assertEquals(expected.picture_id, actual.picture_id)
    assertEquals(expected.minutes_id, actual.minutes_id)
    assertEquals(expected.picture_path, actual.picture_path)
  }

  @Test
  fun insert() {
    val picture = Picture(
      picture_id = 3,
      minutes_id = 1,
      picture_path = "my-picture.jpg",
      time_stamp = "2020-01-23 12:14:47"
    )

    pictureService.insert(picture)

    val expected = FlatXmlDataSetBuilder().build(File(afterData1))
      .getTable("picture")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("picture_id", "time_stamp"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("picture")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("picture_id", "time_stamp"))
      }

    Assertion.assertEquals(expected, actual)
  }

  @Test
  fun update() {
    val picture = Picture(
      picture_id = 1,
      minutes_id = 1,
      picture_path = "my-picture.jpg",
      time_stamp = "2020-01-23 12:14:47"
    )

    pictureService.update(picture)

    val expected = FlatXmlDataSetBuilder().build(File(afterData2))
      .getTable("picture")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("picture_id", "time_stamp"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("picture")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("picture_id", "time_stamp"))
      }

    Assertion.assertEquals(expected, actual)
  }

  @Test
  fun delete() {
    pictureService.delete(1)

    val expected = FlatXmlDataSetBuilder().build(File(afterData3))
      .getTable("picture")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("picture_id", "time_stamp"))
      }

    val actual = databaseTester.connection.createDataSet()
      .getTable("picture")
      .let {
        DefaultColumnFilter.excludedColumnsTable(it, arrayOf("picture_id", "time_stamp"))
      }

    Assertion.assertEquals(expected, actual)
  }
}
