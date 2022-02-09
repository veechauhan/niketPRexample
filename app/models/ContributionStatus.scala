package models

import play.api.libs.json.{ Format, Json }

object ContributionStatus extends Enumeration {

  type ContributionStatus = Value

  val PendingForManager, PendingForUser, Completed = Value

  implicit val ContributionStatusFormat: Format[models.ContributionStatus.Value] = Json.formatEnum(this)

}
