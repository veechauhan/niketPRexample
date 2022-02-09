package repository

import com.google.inject.Inject
import models.Studio
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.{ ExecutionContext, Future }

class StudioTableDef(tag: Tag) extends Table[Studio](tag, "studios") {
  def id                = column[String]("id")
  def studioName        = column[String]("studio_name")
  def studioManagerId   = column[String]("studio_manager_id")
  def disabledTimestamp = column[Long]("disabled_timestamp")

  override def * : ProvenShape[Studio] =
    (
      id.?,
      studioName,
      studioManagerId.?,
      disabledTimestamp
    ) <> (Studio.tupled, Studio.unapply)
}

class StudioRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
  ec: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  private val studio = TableQuery[StudioTableDef]

  def getStudioById(id: String): Future[Option[Studio]] =
    dbConfig.db.run(studio.filter(_.id === id).result.headOption)
}
