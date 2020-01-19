package com.endurance.model

data class Picture(
  val picture_id: Int,
  val minutes_id: Int,
  val picture_path: String,
  val time_stamp: String
)

interface IPictureService {
  fun findPicture(): List<Picture>
  fun findPicture(id: Int): Picture
  fun insertPicture(picture: Picture)
  fun updatePicture(picture: Picture)
  fun deletePicture(id: Int)
}
