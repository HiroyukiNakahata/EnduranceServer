package com.endurance.service

import org.dbunit.JdbcDatabaseTester
import org.dbunit.database.QueryDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.operation.DatabaseOperation
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun saveOriginalData(databaseTester: JdbcDatabaseTester): File {
  val originDataSet = QueryDataSet(databaseTester.connection)
  originDataSet.apply {
    addTable("users")
    addTable("project")
    addTable("minutes")
    addTable("attendee")
    addTable("picture")
    addTable("todo")
  }
  val tmpFile = File.createTempFile("tmp", ".xml", File("./testresources/data/tmp/"))
  FileOutputStream(tmpFile).use {
    FlatXmlDataSet.write(originDataSet, it)
  }
  return tmpFile
}

fun restoreOriginalData(originalData: File, databaseTester: JdbcDatabaseTester) {
  FileInputStream(originalData).use {
    val originalDataSet = FlatXmlDataSetBuilder().build(it)
    DatabaseOperation.CLEAN_INSERT.execute(databaseTester.connection, originalDataSet)
  }
}
