package com.endurance.service

import com.endurance.model.IPictureService
import com.endurance.model.Picture
import java.sql.ResultSet

class PictureService : IPictureService {
  override fun find(): List<Picture> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT picture_id, minutes_id, picture_path, time_stamp
        FROM picture
        ORDER BY picture_id
      """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT picture_id, minutes_id, picture_path, time_stamp
        FROM picture
        WHERE picture_id = ?
      """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT picture_id, minutes_id, picture_path, p.time_stamp
          FROM picture p 
          INNER JOIN minutes m USING (minutes_id)
          WHERE user_id = ?
          ORDER BY p.picture_id
        """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT picture_id, minutes_id, picture_path, p.time_stamp
          FROM picture p 
          INNER JOIN minutes m USING (minutes_id)
          WHERE user_id = ? AND p.picture_id = ?
        """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
          SELECT m.user_id
          FROM picture p
          INNER JOIN minutes m USING (minutes_id)
          WHERE picture_path = ?
        """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        INSERT INTO picture(minutes_id, picture_path, time_stamp)
        VALUES (?, ?, current_timestamp)
      """
      ).use { ps ->
        ps.run {
          setInt(1, picture.minutes_id)
          setString(2, picture.picture_path)
          execute()
        }
      }
    }
  }

  override fun update(picture: Picture) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        UPDATE picture
        SET minutes_id = ?, picture_path = ?, time_stamp = current_timestamp
        WHERE picture_id = ?
      """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        DELETE FROM picture
        WHERE picture_id = ?
      """
      ).use { ps ->
        ps.run {
          setInt(1, pictureId)
          execute()
        }
      }
    }
  }

  override fun deleteByUser(userId: Int, pictureId: Int): String {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
          DELETE FROM picture
          WHERE picture_id = ? AND (
            SELECT user_id
            FROM picture p
            INNER JOIN minutes USING (minutes_id)
            WHERE p.picture_id = ?
          ) = ?
          RETURNING picture_path
        """
      ).use { ps ->
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


class PictureServiceStub : IPictureService {
  override fun find(): List<Picture> {
    return listOf(
      Picture(1, 1, "image.jpg", "2020-01-23 12:14:47")
    )
  }

  override fun find(pictureId: Int): Picture {
    return when (pictureId) {
      1 -> Picture(1, 1, "image.jpg", "2020-01-23 12:14:47")
      else -> Picture()
    }
  }

  override fun findByUser(userId: Int): List<Picture> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun findByUser(userId: Int, pictureId: Int): Picture {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun findUserIdByPicturePath(picturePath: String): Int {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun insert(picture: Picture) {}
  override fun update(picture: Picture) {}
  override fun delete(pictureId: Int) {}
  override fun deleteByUser(userId: Int, pictureId: Int): String = "image.png"
}
