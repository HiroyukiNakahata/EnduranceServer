package com.endurance.authentication

import java.security.MessageDigest

object HashUtil {

  fun sha512(input: String) = hashString("SHA-512", input)

  fun sha256(input: String) = hashString("SHA-256", input)

  private fun hashString(type: String, input: String): String {
    val hexChars = "0123456789ABCDEF"
    val bytes = MessageDigest
      .getInstance(type)
      .digest(input.toByteArray())

    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
      val i = it.toInt()
      result.append(hexChars[i shr 4 and 0x0f])
      result.append(hexChars[i and 0x0f])
    }

    return result.toString()
  }
}
