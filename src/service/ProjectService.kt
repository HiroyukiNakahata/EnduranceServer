package com.endurance.service

import com.endurance.model.IProjectService
import com.endurance.model.Project
import java.sql.ResultSet

class ProjectService : IProjectService {
  override fun find(): List<Project> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT project_id, project_name, client
        FROM project
        ORDER BY project_id
      """
      ).use { ps ->
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
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT project_id, project_name, client
        FROM project
        WHERE project_id = ?
      """
      ).use { ps ->
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

  override fun insert(project: Project) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        INSERT INTO project(project_name, client) 
        VALUES (?, ?)
      """
      ).use { ps ->
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
      con.prepareStatement(
        """
        UPDATE project
        SET project_name = ?, client = ?
        WHERE project_id = ?
      """
      ).use { ps ->
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
      con.prepareStatement(
        """
        DELETE FROM project
        WHERE project_id = ?
      """
      ).use { ps ->
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

  override fun insert(project: Project) {}
  override fun update(project: Project) {}
  override fun delete(id: Int) {}
}
