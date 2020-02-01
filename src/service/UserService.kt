package com.endurance.service

import com.endurance.model.IUserService
import com.endurance.model.User
import java.sql.ResultSet

class UserService : IUserService {
  override fun find(): List<User> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT user_id, first_name, last_name, mail_address
        FROM users
        ORDER BY user_id
      """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT user_id, first_name, last_name, mail_address
        FROM users
        WHERE user_id = ?
      """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT password, user_id
          FROM users
          WHERE mail_address = ?
        """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        INSERT INTO users(first_name, last_name, mail_address, password)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (mail_address)
        DO UPDATE SET first_name = ?, last_name = ?, password = ?
      """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        UPDATE users
        SET first_name = ?, last_name = ?, mail_address = ?
        WHERE user_id = ?
      """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        DELETE FROM users
        WHERE user_id = ?
      """
      ).use { ps ->
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


class UserServiceStub : IUserService {
  override fun find(): List<User> {
    return listOf(
      User(1, "test", "test", "test@sample.com")
    )
  }

  override fun find(id: Int): User {
    return when (id) {
      1 -> User(1, "test", "test", "test@sample.com")
      else -> User()
    }
  }

  override fun findPasswordByMailAddress(mail_address: String): Pair<String, Int> {
    return Pair(
      "9FD89A274AE758D9D8D98588C367B6C5C77F3C67EF58B26F1AB432EB56EBC0377C80DF2161151A132C69E9039E8DF4B022C28D6C1F0D0FFE66631701993B5582",
      1
    )
  }

  override fun insert(user: User, password: String) {}
  override fun update(user: User) {}
  override fun delete(id: Int) {}
}
