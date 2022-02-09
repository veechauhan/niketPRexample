package repository

import com.google.inject.{ Inject, Singleton }
import models.ContributionStatus.ContributionStatus
import models.ContributionType.ContributionType
import models.{ Contribution, ContributionStatus, ContributionType, Remarks }
import models._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.libs.json.Json
import slick.ast.BaseTypedType
import slick.jdbc.{ JdbcProfile, JdbcType }
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape
import utils.TimeUtility

import java.time.Instant

import java.util.{ Calendar, Date }

import scala.concurrent.{ ExecutionContext, Future }

object ContributionsMapper {

  implicit val remarkMapper: JdbcType[List[Remark]] with BaseTypedType[List[Remark]] =
    MappedColumnType.base[List[Remark], String](
      remarks => Json.toJson(remarks).toString(),
      remarkAsString => Json.parse(remarkAsString).as[List[Remark]]
    )

  implicit val contributionTypeMapper: JdbcType[ContributionType] with BaseTypedType[ContributionType] =
    MappedColumnType.base[ContributionType, String](
      c => c.toString,
      e => ContributionType.withName(e)
    )

  implicit val contributionStatusMapper: JdbcType[ContributionStatus] with BaseTypedType[ContributionStatus] =
    MappedColumnType.base[ContributionStatus, String](
      c => c.toString,
      e => ContributionStatus.withName(e)
    )

}

class ContributionTableDef(tag: Tag) extends Table[Contribution](tag, "contributions") {

  import ContributionsMapper._

  def id: Rep[String] = column[String]("id")

  def contributionName: Rep[String] = column[String]("contribution_name")

  def contributionUrl: Rep[String] = column[String]("contribution_url")

  def createdOn: Rep[Long] = column[Long]("created_on")

  def lastUpdateOn: Rep[Long] = column[Long]("last_update_on")

  def userId: Rep[String] = column[String]("user_id")

  def studioId: Rep[String] = column[String]("studio_id")

  def contributionType: Rep[ContributionType] = column[ContributionType]("contribution_type")

  def status: Rep[ContributionStatus] = column[ContributionStatus]("status")

  def remarks: Rep[List[Remark]] = column[List[Remark]]("remarks")

  def disableTimestamp: Rep[Long] = column[Long]("disabled_timestamp")

  override def * : ProvenShape[Contribution] =
    (
      id.?,
      contributionName,
      contributionUrl,
      createdOn,
      lastUpdateOn,
      userId,
      studioId,
      contributionType,
      status,
      remarks,
      disableTimestamp
    ) <> ((Contribution.apply _).tupled, Contribution.unapply)
}

@Singleton
class ContributionRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
  ec: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import ContributionsMapper._

  private val contribution = TableQuery[ContributionTableDef]

  def getContributionsById(id: String, offset: Int, size: Int): Future[Seq[Contribution]] =
    dbConfig.db.run(contribution.filter(_.userId === id).sortBy(_.createdOn.desc).drop(offset).take(size).result)

  def getContributionsCountByUserId(userId: String): Future[Int] = {
    val query = contribution.filter(c => c.userId === userId).length.result
    dbConfig.db.run(query)
  }

  def getContributionsByUserId(userId: String): Future[Seq[Contribution]] =
    dbConfig.db.run(contribution.filter(_.userId === userId).result)

  def techHubTemplateCountByUserId(userId: String): Future[Int] =
    dbConfig.db.run(
      contribution
        .filter(c =>
          c.contributionType === ContributionType.TechHubTemplate &&
            c.status === ContributionStatus.Completed && c.userId === userId
        )
        .length
        .result
    )

  def blogCountByUserId(userId: String): Future[Int] =
    dbConfig.db.run(
      contribution
        .filter(c =>
          c.contributionType === ContributionType.Blog && c.status === ContributionStatus.Completed && c.userId === userId
        )
        .length
        .result
    )


  def openSourceCountByUserId(userId: String): Future[Int] =
    dbConfig.db.run(
      contribution
        .filter(c =>
          c.contributionType === ContributionType.OpenSource && c.status === ContributionStatus.Completed && c.userId === userId
        )
        .length
        .result
    )

  def certificationCountByUserId(userId: String): Future[Int] =
    dbConfig.db.run(
      contribution
        .filter(c =>
          c.contributionType === ContributionType.Certification && c.status === ContributionStatus.Completed && c.userId === userId
        )
        .length
        .result
    )

  def blogCountForCurrentMonthByUserId(userId: String): Future[Int] =
    dbConfig.db.run(
      contribution
        .filter(c =>
          c.contributionType === ContributionType.Blog && c.userId === userId && c.status === ContributionStatus.Completed &&
            c.createdOn >= TimeUtility.getTimestampOfFirstOfCurrentMonth(TimeZone.IST)
        )
        .length
        .result
    )

  def techHubTemplateCountForCurrentMonthByUserId(userId: String): Future[Int] =
    dbConfig.db.run(
      contribution
        .filter(c =>
          c.contributionType === ContributionType.TechHubTemplate && c.userId === userId && c.status === ContributionStatus.Completed &&
            c.createdOn >= TimeUtility.getTimestampOfFirstOfCurrentMonth(TimeZone.IST)
        )
        .length
        .result
    )

  def certificationCountForCurrentMonthByUserId(userId: String): Future[Int] =
    dbConfig.db.run(
      contribution
        .filter(c =>
          c.contributionType === ContributionType.Certification &&
            c.userId === userId &&
            c.status === ContributionStatus.Completed &&
            c.createdOn >= TimeUtility.getTimestampOfFirstOfCurrentMonth(TimeZone.IST)
        )
        .length
        .result
    )

  def openSourceCountForCurrentMonthByUserId(userId: String): Future[Int] =
    dbConfig.db.run(
      contribution
        .filter(c =>
          c.contributionType === ContributionType.OpenSource && c.userId === userId &&
            c.createdOn >= TimeUtility.getTimestampOfFirstOfCurrentMonth(TimeZone.IST)
        )
        .length
        .result
    )

  def getRecentContributionsByUserId(userId: String): Future[Seq[Contribution]] =
    dbConfig.db.run(contribution.filter(_.userId === userId).sortBy(_.createdOn.desc).take(5).result)


  def addNewContribution(newContribution: Contribution): Future[Int] = {
    dbConfig.db.run(contribution += newContribution)
  }
}
