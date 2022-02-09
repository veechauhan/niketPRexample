package repository

import com.google.inject.Inject
import models.Project
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext

class ProjectTableDef(tag: Tag) extends Table[Project](tag, "projects") {
  def id: Rep[String]          = column[String]("id")
  def projectName: Rep[String] = column[String]("projectName")
  def projectInfo: Rep[String] = column[String]("projectInfo")
  def studioId: Rep[String]    = column[String]("studioId")
  def disabledTimestamp        = column[Long]("disabledTimestamp")

  override def * : ProvenShape[Project] =
    (
      id.?,
      projectName,
      projectInfo,
      studioId,
      disabledTimestamp
    ) <> (Project.tupled, Project.unapply)
}

class ProjectRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
  ec: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  val project = TableQuery[ProjectTableDef]
}
