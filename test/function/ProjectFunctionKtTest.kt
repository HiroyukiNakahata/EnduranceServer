package function

import com.endurance.function.isEmptyProject
import com.endurance.model.Project
import org.junit.Test
import org.hamcrest.core.Is.*
import org.junit.Assert.*

class ProjectFunctionKtTest {

  @Test
  fun isNotEmptyProject_1() {
    val project = Project(0, "test", "hoge")
    val actual = isEmptyProject(project)
    val expected = true
    assertThat(actual, `is`(expected))
  }

  @Test
  fun isNotEmptyProject_2() {
    val project = Project(0, "", "")
    val actual = isEmptyProject(project)
    val expected = false
    assertThat(actual, `is`(expected))
  }

  @Test
  fun isNotEmptyProject_3() {
    val project = Project(0, "", "hoge")
    val actual = isEmptyProject(project)
    val expected = false
    assertThat(actual, `is`(expected))
  }
}
