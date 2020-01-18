package com.endurance.service

import com.zaxxer.hikari.*
import java.sql.Connection

object HikariService {
  private val config = HikariConfig()
  private val ds: HikariDataSource

  init {
    config.jdbcUrl = "jdbc:postgresql://localhost/endurance"
    config.username = "postgres"
    config.password = "1203"
    ds = HikariDataSource(config)
  }

  fun getConnection(): Connection {
    return ds.connection
  }
}
