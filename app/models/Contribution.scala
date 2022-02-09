package models

import models.ContributionStatus.ContributionStatus
import models.ContributionType.ContributionType
import play.api.libs.json.{ Json, OFormat }

case class Contribution(
  id: Option[String],
  contributionName: String,
  contributionUrl: String,
  createdOn: Long,
  lastUpdateOn: Long,
  userId: String,
  studioId: String,
  contributionType: ContributionType,
  status: ContributionStatus,
  remarks: List[Remark],
  disabledTimestamp: Long
)

object Contribution {
  val ContributionFormat: OFormat[Contribution] = Json.format[Contribution]
}
