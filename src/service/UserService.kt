package com.endurance.service

import com.endurance.model.IUserService
import com.endurance.model.User
import java.sql.ResultSet


class UserService : IUserService {
  override fun find(): List<User> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT user_id, family_name, last_name, mail_address
        FROM "user"
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
        SELECT user_id, family_name, last_name, mail_address
        FROM "user"
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

  override fun insert(user: User) {
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

  override fun update(user: User) {
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

  override fun delete(id: Int) {
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

  private fun rowsToUser(rows: ResultSet): User = User(
    rows.getInt(1),
    rows.getString(2),
    rows.getString(3),
    rows.getString(4)
  )
}
