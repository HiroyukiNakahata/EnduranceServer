package com.endurance.service

import com.endurance.model.IUserService
import com.endurance.model.User
import java.sql.SQLException

class UserService : IUserService {
  override fun findUser(): List<User> {
    HikariService.getConnection().use { con ->
      con.prepareStatement("""
        SELECT user_id, family_name, last_name, mail_address
        FROM "user"
        ORDER BY user_id
      """).use { ps ->
        ps.executeQuery().use { rows ->
          val users = mutableListOf<User>()
          while (rows.next()) {
            users.add(User(
              rows.getInt(1),
              rows.getString(2),
              rows.getString(3),
              rows.getString(4)
            ))
          }
          return users
        }
      }
    }
  }

  override fun findUser(id: Int): User {
    HikariService.getConnection().use { con ->
      con.prepareStatement("""
        SELECT user_id, family_name, last_name, mail_address
        FROM "user"
        WHERE user_id = ?
      """).use { ps ->
        ps.setInt(1, id)
        ps.executeQuery().use { rows ->
          rows.next()
          return try {
            User(
              rows.getInt(1),
              rows.getString(2),
              rows.getString(3),
              rows.getString(4)
            )
          } catch (e: SQLException) {
            User(0, "", "", "")
          }
        }
      }
    }
  }

  override fun insertUser(user: User) {
    HikariService.getConnection().use { con ->
      con.prepareStatement("""
        INSERT INTO "user"(family_name, last_name, mail_address)
        VALUES (?, ?, ?)
        ON CONFLICT (mail_address)
        DO UPDATE SET family_name=?, last_name=?
      """).use { ps ->
        ps.setString(1, user.family_name)
        ps.setString(2, user.last_name)
        ps.setString(3, user.mail_address)
        ps.setString(4, user.family_name)
        ps.setString(5, user.last_name)
        ps.execute()
      }
    }
  }

  override fun updateUser(user: User) {
    HikariService.getConnection().use { con ->
      con.prepareStatement("""
        UPDATE "user"
        SET family_name=?, last_name=?, mail_address=?
        WHERE user_id=?
      """).use { ps ->
        ps.setString(1, user.family_name)
        ps.setString(2, user.last_name)
        ps.setString(3, user.mail_address)
        ps.setInt(4, user.user_id)
        ps.execute()
      }
    }
  }

  override fun deleteUser(id: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement("""
        DELETE FROM "user"
        WHERE user_id=?
      """).use { ps ->
        ps.setInt(1, id)
        ps.execute()
      }
    }
  }
}
