package com.endurance.service

import com.endurance.model.Picture
import com.endurance.model.IPictureService


class PictureService : IPictureService {
  override fun findPicture(): List<Picture> {
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
              rows.next() -> Picture(
                rows.getInt(1),
                rows.getInt(2),
                rows.getString(3),
                rows.getString(4)
              )
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun findPicture(id: Int): Picture {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT picture_id, minutes_id, picture_path, time_stamp
        FROM picture
        WHERE picture_id = ?
      """
      ).use { ps ->
        ps.setInt(1, id)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> Picture(
              rows.getInt(1),
              rows.getInt(2),
              rows.getString(3),
              rows.getString(4)
            )
            else -> Picture()
          }
        }
      }
    }
  }

  override fun insertPicture(picture: Picture) {
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

  override fun updatePicture(picture: Picture) {
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

  override fun deletePicture(id: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        DELETE FROM picture
        WHERE picture_id = ?
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
