package com.endurance.service

import com.google.gson.Gson
import com.zaxxer.hikari.*
import java.io.File
import java.sql.Connection

object HikariService {
  private val config = HikariConfig()
  private val ds: HikariDataSource

  init {
    val databaseConfig = readConfig()
    config.apply {
      driverClassName = databaseConfig.driverClass
      jdbcUrl = databaseConfig.jdbcUrl
      username = databaseConfig.username
      password = databaseConfig.password
    }
    ds = HikariDataSource(config)
  }

  fun getConnection(): Connection {
    return ds.connection
  }

  fun readConfig(): DatabaseConfig {
    val json = File("./resources/database.json").readText()
    return Gson().fromJson(json, DatabaseConfig::class.java)
  }
}

data class DatabaseConfig(
  val driverClass: String,
  val jdbcUrl: String,
  val username: String,
  val password: String
)
