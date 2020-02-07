package com.endurance.service

import com.endurance.model.IProjectService
import com.endurance.model.Project
import org.intellij.lang.annotations.Language
import java.sql.ResultSet

class ProjectService : IProjectService {
  override fun find(): List<Project> {
    val query = selectQuery + "ORDER BY project_id"

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.executeQuery().use { rows ->
          return generateSequence {
            when {
              rows.next() -> rowsToProject(rows)
              else -> null
            }
          }.toList()
        }
      }
    }
  }

  override fun find(id: Int): Project {
    val query = selectQuery + "WHERE project_id = ?"

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.setInt(1, id)
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rowsToProject(rows)
            else -> Project()
          }
        }
      }
    }
  }

  override fun count(): Int {
    HikariService.getConnection().use { con ->
      con.prepareStatement(countQuery).use { ps ->
        ps.executeQuery().use { rows ->
          return when {
            rows.next() -> rows.getInt(1)
            else -> 0
          }
        }
      }
    }
  }

  override fun insert(project: Project) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(insertQuery).use { ps ->
        ps.run {
          setString(1, project.project_name)
          setString(2, project.client)
          execute()
        }
      }
    }
  }

  override fun update(project: Project) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(updateQuery).use { ps ->
        ps.run {
          setString(1, project.project_name)
          setString(2, project.client)
          setInt(3, project.project_id)
          execute()
        }
      }
    }
  }

  override fun delete(id: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(deleteQuery).use { ps ->
        ps.run {
          setInt(1, id)
          execute()
        }
      }
    }
  }

  private fun rowsToProject(rows: ResultSet): Project = Project(
    rows.getInt(1),
    rows.getString(2),
    rows.getString(3)
  )


  @Language("SQL")
  private val selectQuery = """
    SELECT project_id,
           project_name,
           client
    FROM project
  """

  @Language("SQL")
  private val countQuery = """
    SELECT COUNT(*)
    FROM project
  """

  @Language("SQL")
  private val insertQuery = """
    INSERT INTO project(project_name, client) 
    VALUES (?, ?)
  """

  @Language("SQL")
  private val updateQuery = """
    UPDATE project
    SET project_name = ?, client = ?
    WHERE project_id = ?
  """

  @Language("SQL")
  private val deleteQuery = """
    DELETE FROM project
    WHERE project_id = ?
  """
}


class ProjectServiceStub : IProjectService {
  override fun find(): List<Project> {
    return listOf(
      Project(1, "test", "sample")
    )
  }

  override fun find(id: Int): Project {
    return when (id) {
      1 -> Project(1, "test", "sample")
      else -> Project()
    }
  }

  override fun count(): Int {
    return 1
  }

  override fun insert(project: Project) {}
  override fun update(project: Project) {}
  override fun delete(id: Int) {}
}
