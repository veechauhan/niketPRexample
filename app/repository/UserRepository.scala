package repository

import com.google.inject.{ Inject, Singleton }
import models.UserRole.UserRole
import models.{ User, UserRole }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.ast.BaseTypedType
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.{ JdbcProfile, JdbcType }
import slick.lifted.ProvenShape

import scala.concurrent.{ ExecutionContext, Future }

class UserTableDef(tag: Tag) extends Table[User](tag, "users") {

  implicit val roleMapper: JdbcType[UserRole] with BaseTypedType[UserRole] = MappedColumnType.base[UserRole, String](
    e => e.toString,
    s => UserRole.withName(s)
  )

  def id: Rep[String] = column[String]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  def email: Rep[String] = column[String]("email")

  def bio: Rep[String] = column[String]("bio")

  def mobileNumber: Rep[String] = column[String]("mobile_number")

  def department: Rep[String] = column[String]("department")

  def managerId: Rep[String] = column[String]("manager_id")

  def githubId: Rep[String] = column[String]("github_id")

  def role: Rep[UserRole] = column[UserRole]("role")

  def projectId: Rep[String] = column[String]("project_id")

  def studioId: Rep[String] = column[String]("studio_id")

  def disableTimestamp: Rep[Long] = column[Long]("disabled_timestamp")

  override def * : ProvenShape[User] =
    (
      id.?,
      name,
      email,
      bio.?,
      mobileNumber.?,
      managerId.?,
      githubId.?,
      role,
      projectId.?,
      studioId.?,
      disableTimestamp
    ) <> ((User.apply _).tupled, User.unapply)
}

@Singleton
class UserRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
  ec: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  private val users = TableQuery[UserTableDef]

  def getUserById(id: String): Future[Option[User]] =
    dbConfig.db.run(
      users.filter(_.id === id).result.headOption
    )

  def getUserByStudioId(studioId: String, offset: Int): Future[Seq[User]] =
    dbConfig.db.run(
      users.filter(_.studioId === studioId).drop(offset).take(50).result
    )

  def createNewUser(id: String, email: String, name: String): Future[Int] = {
    val user = User(Some(id), name, email)
    dbConfig.db.run(users += user)
  }

}
