package com.endurance.model

data class Picture(
  val picture_id: Int,
  val minutes_id: Int,
  val picture_path: String,
  val time_stamp: String
) {
  constructor(): this(0, 0, "", "")
}

interface IPictureService {
  fun find(): List<Picture>
  fun find(pictureId: Int): Picture
  fun findByUser(userId: Int): List<Picture>
  fun findByUser(userId: Int, pictureId: Int): Picture
  fun findUserIdByPicturePath(picturePath: String): Int
  fun insert(picture: Picture)
  fun update(picture: Picture)
  fun delete(pictureId: Int)
  fun delete(userId: Int, pictureId: Int): String
}
