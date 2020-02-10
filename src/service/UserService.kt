package com.endurance.service

import com.endurance.model.IUserService
import com.endurance.model.User
import org.intellij.lang.annotations.Language
import java.sql.ResultSet

class UserService : IUserService {

  override fun find(): List<User> {
    @Language("SQL")
    val query = """
      SELECT user_id, first_name, last_name, mail_address
      FROM users
      ORDER BY user_id
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToUser(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun find(id: Int): User {
    @Language("SQL")
    val query = """
      SELECT user_id, first_name, last_name, mail_address
      FROM users
      WHERE user_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, id)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rowsToUser(rows)
            else -> User()
          }
        }
      }
    }
  }

  override fun findPasswordByMailAddress(mail_address: String): Pair<String, Int> {
    @Language("SQL")
    val query = """
      SELECT password, user_id
      FROM users
      WHERE mail_address = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setString(1, mail_address)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> Pair(rows.getString(1), rows.getInt(2))
            else -> Pair("", 0)
          }
        }
      }
    }
  }

  override fun insert(user: User, password: String) {
    @Language("SQL")
    val query = """
      INSERT INTO users(first_name, last_name, mail_address, password)
      VALUES (?, ?, ?, ?)
      ON CONFLICT (mail_address)
      DO UPDATE SET first_name = ?, last_name = ?, password = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setString(1, user.first_name)
          setString(2, user.last_name)
          setString(3, user.mail_address)
          setString(4, password)
          setString(5, user.first_name)
          setString(6, user.last_name)
          setString(7, password)
          execute()
        }
      }
    }
  }

  override fun update(user: User) {
    @Language("SQL")
    val query = """
      UPDATE users
      SET first_name = ?, last_name = ?, mail_address = ?
      WHERE user_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setString(1, user.first_name)
          setString(2, user.last_name)
          setString(3, user.mail_address)
          setInt(4, user.user_id)
          execute()
        }
      }
    }
  }

  override fun delete(id: Int) {
    @Language("SQL")
    val query = """
      DELETE FROM users
      WHERE user_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, id)
          execute()
        }
      }
    }
  }

  private fun rowsToUser(rows: ResultSet): User = User(
    rows.getInt(1),
    rows.getString(2),
    rows.getString(3),
    rows.getString(4)
  )
}
