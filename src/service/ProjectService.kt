package com.endurance.service

import com.endurance.model.Project
import com.endurance.model.IProjectService
import java.sql.SQLException


class ProjectService : IProjectService {
  override fun findProject(): List<Project> {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        SELECT project_id, project_name, client
        FROM project
        ORDER BY project_id
      """
      ).use { ps ->
        ps.executeQuery().use { rows ->
          val projects = mutableListOf<Project>()
          while (rows.next()) {
            projects.add(
              Project(
                rows.getInt(1),
                rows.getString(2),
                rows.getString(3)
              )
            )
          }
          return projects
        }
      }
    }
  }

  override fun findProject(id: Int): Project {
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
          rows.next()
          return try {
            Project(
              rows.getInt(1),
              rows.getString(2),
              rows.getString(3)
            )
          } catch (e: SQLException) {
            Project(0, "", "")
          }
        }
      }
    }
  }

  override fun insertProject(project: Project) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        INSERT INTO project(project_name, client) 
        VALUES (?, ?)
      """
      ).use { ps ->
        ps.setString(1, project.project_name)
        ps.setString(2, project.client)
        ps.execute()
      }
    }
  }

  override fun updateProject(project: Project) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        UPDATE project
        SET project_name = ?, client = ?
        WHERE project_id = ?
      """
      ).use { ps ->
        ps.setString(1, project.project_name)
        ps.setString(2, project.client)
        ps.setInt(3, project.project_id)
        ps.execute()
      }
    }
  }

  override fun deleteProject(id: Int) {
    HikariService.getConnection().use { con ->
      con.prepareStatement(
        """
        DELETE FROM project
        WHERE project_id = ?
      """
      ).use { ps ->
        ps.setInt(1, id)
        ps.execute()
      }
    }
  }
}
