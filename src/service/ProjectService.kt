package com.endurance.service

import com.endurance.model.IProjectService
import com.endurance.model.Project
import org.intellij.lang.annotations.Language
import java.sql.ResultSet

class ProjectService : IProjectService {

  override fun find(): List<Project> {
    @Language("SQL")
    val query = """
      SELECT project_id, project_name, client
      FROM project
      ORDER BY project_id
    """

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
    @Language("SQL")
    val query = """
      SELECT project_id, project_name, client
      FROM project
      WHERE project_id = ?
    """

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
    @Language("SQL")
    val query = """
      SELECT COUNT(*)
      FROM project
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
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
    @Language("SQL")
    val query = """
      INSERT INTO project(project_name, client) 
      VALUES (?, ?)
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
        ps.run {
          setString(1, project.project_name)
          setString(2, project.client)
          execute()
        }
      }
    }
  }

  override fun update(project: Project) {
    @Language("SQL")
    val query = """
      UPDATE project
      SET project_name = ?, client = ?
      WHERE project_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
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
    @Language("SQL")
    val query = """
      DELETE FROM project
      WHERE project_id = ?
    """

    HikariService.getConnection().use { con ->
      con.prepareStatement(query).use { ps ->
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
}
