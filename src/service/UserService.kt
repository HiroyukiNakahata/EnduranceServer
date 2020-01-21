package com.endurance.service

import com.endurance.model.IUserService
import com.endurance.model.User


class UserService : IUserService {
  override fun findUser(): List<User> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT user_id, family_name, last_name, mail_address
        FROM "user"
        ORDER BY user_id
      """
      ).use { ps ->
        ps.executeQuery().use { rows ->
          val users = mutableListOf<User>()
          while (rows.next()) users.add(
            User(
              rows.getInt(1),
              rows.getString(2),
              rows.getString(3),
              rows.getString(4)
            )
          )
          return users
        }
      }
    }
  }

  override fun findUser(id: Int): User {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT user_id, family_name, last_name, mail_address
        FROM "user"
        WHERE user_id = ?
      """
      ).use { ps ->
        ps.setInt(1, id)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> User(
              rows.getInt(1),
              rows.getString(2),
              rows.getString(3),
              rows.getString(4)
            )
            else -> User()
          }
        }
      }
    }
  }

  override fun insertUser(user: User) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        INSERT INTO "user"(family_name, last_name, mail_address)
        VALUES (?, ?, ?)
        ON CONFLICT (mail_address)
        DO UPDATE SET family_name=?, last_name=?
      """
      ).use { ps ->
        ps.run {
          setString(1, user.family_name)
          setString(2, user.last_name)
          setString(3, user.mail_address)
          setString(4, user.family_name)
          setString(5, user.last_name)
          execute()
        }
      }
    }
  }

  override fun updateUser(user: User) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        UPDATE "user"
        SET family_name=?, last_name=?, mail_address=?
        WHERE user_id=?
      """
      ).use { ps ->
        ps.run {
          setString(1, user.family_name)
          setString(2, user.last_name)
          setString(3, user.mail_address)
          setInt(4, user.user_id)
          execute()
        }
      }
    }
  }

  override fun deleteUser(id: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        DELETE FROM "user"
        WHERE user_id=?
      """
      ).use { ps ->
        ps.run {
          setInt(1, id)
          execute()
        }
      }
    }
  }
}
