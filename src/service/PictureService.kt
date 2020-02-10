package com.endurance.service

import com.endurance.model.IPictureService
import com.endurance.model.Picture
import org.intellij.lang.annotations.Language
import java.sql.ResultSet

class PictureService : IPictureService {

  override fun find(): List<Picture> {
    @Language("SQL")
    val query = """
      SELECT picture_id, minutes_id, picture_path, time_stamp
      FROM picture
      ORDER BY picture_id
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToPicture(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun find(pictureId: Int): Picture {
    @Language("SQL")
    val query = """
      SELECT picture_id, minutes_id, picture_path, time_stamp
      FROM picture
      WHERE picture_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, pictureId)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rowsToPicture(rows)
            else -> Picture()
          }
        }
      }
    }
  }

  override fun findByUser(userId: Int): List<Picture> {
    @Language("SQL")
    val query = """
      SELECT picture_id, minutes_id, picture_path, p.time_stamp
      FROM picture p 
      INNER JOIN minutes m USING (minutes_id)
        WHERE user_id = ?
        ORDER BY p.picture_id
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, userId)
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToPicture(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun findByUser(userId: Int, pictureId: Int): Picture {
    @Language("SQL")
    val query = """
      SELECT picture_id, minutes_id, picture_path, p.time_stamp
      FROM picture p 
      INNER JOIN minutes m USING (minutes_id)
        WHERE user_id = ? AND p.picture_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, userId)
        ps.setInt(2, pictureId)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rowsToPicture(rows)
            else -> Picture()
          }
        }
      }
    }
  }

  override fun findUserIdByPicturePath(picturePath: String): Int {
    @Language("SQL")
    val query = """
      SELECT m.user_id
      FROM picture p
      INNER JOIN minutes m USING (minutes_id)
        WHERE picture_path = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setString(1, picturePath)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rows.getInt(1)
            else -> 0
          }
        }
      }
    }
  }

  override fun insert(picture: Picture) {
    @Language("SQL")
    val query = """
      INSERT INTO picture(minutes_id, picture_path, time_stamp)
      VALUES (?, ?, current_timestamp)
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, picture.minutes_id)
          setString(2, picture.picture_path)
          execute()
        }
      }
    }
  }

  override fun update(picture: Picture) {
    @Language("SQL")
    val query = """
      UPDATE picture
      SET minutes_id = ?, picture_path = ?, time_stamp = current_timestamp
      WHERE picture_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, picture.minutes_id)
          setString(2, picture.picture_path)
          setInt(3, picture.picture_id)
          execute()
        }
      }
    }
  }

  override fun delete(pictureId: Int) {
    @Language("SQL")
    val query = """
      DELETE FROM picture
      WHERE picture_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, pictureId)
          execute()
        }
      }
    }
  }

  override fun delete(userId: Int, pictureId: Int): String {
    @Language("SQL")
    val query = """
      DELETE FROM picture
      WHERE picture_id = ? AND (
        SELECT user_id
        FROM picture p
        INNER JOIN minutes USING (minutes_id)
        WHERE p.picture_id = ?) = ?
      RETURNING picture_path
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setInt(1, pictureId)
          setInt(2, pictureId)
          setInt(3, userId)
          executeQuery().use { rows ->
            return when {
              rows.next() -> rows.getString(1)
              else -> ""
            }
          }
        }
      }
    }
  }

  private fun rowsToPicture(rows: ResultSet): Picture = Picture(
    rows.getInt(1),
    rows.getInt(2),
    rows.getString(3),
    rows.getString(4)
  )
}
