package function

import com.endurance.function.*
import com.endurance.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

class IsEmptyFuncsKtTest {

  // 正常系
  @Test
  fun isEmptyAttendee_1() {
    val attendee = Attendee(1, 1, "tanaka", "shibuya.inc")
    val actual = isEmptyAttendee(attendee)
    val expected = false
    assertEquals(expected, actual)
  }

  // 異常系
  @Test
  fun isEmptyAttendee_2() {
    val attendee = Attendee(1, 1, "", "")
    val actual = isEmptyAttendee(attendee)
    val expected = true
    assertEquals(expected, actual)
  }

  // 正常系
  @Test
  fun isEmptyMinutes_1() {
    val minutes = Minutes(1, 1, 1, "shibuya", "おひるごはん",
      "おひるごはん", "おひるごはん", "2020-01-01")
    val actual = isEmptyMinutes(minutes)
    val expected = false
    assertEquals(expected, actual)
  }

  // 異常系
  @Test
  fun isEmptyMinutes_2() {
    val minutes = Minutes(1, 1, 1, "", "",
      "", "", "2020-01-01")
    val actual = isEmptyMinutes(minutes)
    val expected = true
    assertEquals(expected, actual)
  }

  // 正常系
  @Test
  fun isEmptyPicture_1() {
    val picture = Picture(1, 1, "image.png", "2020-01-01")
    val actual = isEmptyPicture(picture)
    val expected = false
    assertEquals(expected, actual)
  }

  // 異常系
  @Test
  fun isEmptyPicture_2() {
    val picture = Picture(1, 0, "", "")
    val actual = isEmptyPicture(picture)
    val expected = true
    assertEquals(expected, actual)
  }

  // 正常系
  @Test
  fun isEmptyProject_1() {
    val project = Project(1, "おひるごはん", "shinagawa.inc")
    val actual = isEmptyProject(project)
    val expected = false
    assertEquals(expected, actual)
  }

  // 異常系
  @Test
  fun isEmptyProject_2() {
    val project = Project(1, "", "")
    val actual = isEmptyProject(project)
    val expected = true
    assertEquals(expected, actual)
  }

  // 正常系
  @Test
  fun isEmptyTodo_1() {
    val todo = Todo(1, 1, 1, 1, "hoge", "fuga",
      "hoge", "fuga", false)
    val actual = isEmptyTodo(todo)
    val expected = false
    assertEquals(expected, actual)
  }

  // 異常系
  @Test
  fun isEmptyTodo_2() {
    val todo = Todo(1, 1, 1, 1, "", "",
      "", "", false)
    val actual = isEmptyTodo(todo)
    val expected = true
    assertEquals(expected, actual)
  }

  // 正常系
  @Test
  fun isEmptyUser_1() {
    val user = User(1, "shibuya", "nakano", "shibuya@sample.com")
    val actual = isEmptyUser(user)
    val expected = false
    assertEquals(expected, actual)
  }

  // 異常系
  @Test
  fun isEmptyUser_2() {
    val user = User(1, "", "", "")
    val actual = isEmptyUser(user)
    val expected = true
    assertEquals(expected, actual)
  }
}
