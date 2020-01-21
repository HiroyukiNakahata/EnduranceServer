package function

import com.endurance.function.isEmptyUser
import com.endurance.model.User
import org.junit.Test
import org.hamcrest.core.Is.*
import org.junit.Assert.*

class UserFunctionKtTest {

    @Test
    fun checkEmptyUser_1() {
        val user = User(0, "hoge", "fuga", "test@test.com")
        val expected = true
        val actual = isEmptyUser(user)
        assertThat(actual, `is`(expected))
    }

    @Test
    fun checkEmptyUser_2() {
        val user = User(0, "", "", "")
        val expected = false
        val actual = isEmptyUser(user)
        assertThat(actual, `is`(expected))
    }

    @Test
    fun checkEmptyUser_3() {
        val user = User(0, "", "hoge", "test@mail.com")
        val expected = false
        val actual = isEmptyUser(user)
        assertThat(actual, `is`(expected))
    }
}
