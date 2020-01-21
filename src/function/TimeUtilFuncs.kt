package com.endurance.function

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getNowTimeString(): String {
  val date = LocalDateTime.now()
  val fmt = DateTimeFormatter.ofPattern( "yyyy-MM-dd_HH-mm-ss-SSS" )
  return date.format(fmt)
}
