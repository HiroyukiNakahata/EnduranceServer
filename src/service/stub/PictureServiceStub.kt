package com.endurance.service.stub

import com.endurance.model.IPictureService
import com.endurance.model.Picture

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
    return listOf(
      Picture(1, 1, "image.jpg", "2020-01-23 12:14:47")
    )
  }

  override fun findByUser(userId: Int, pictureId: Int): Picture {
    return when (pictureId) {
      1 -> Picture(1, 1, "image.jpg", "2020-01-23 12:14:47")
      else -> Picture()
    }
  }

  override fun findUserIdByPicturePath(picturePath: String): Int {
    return 1
  }

  override fun insert(picture: Picture) {}
  override fun update(picture: Picture) {}
  override fun delete(pictureId: Int) {}
  override fun delete(userId: Int, pictureId: Int): String = "image.png"
}
