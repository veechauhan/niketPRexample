package models

import models.ContributionStatus.ContributionStatus
import models.ContributionType.ContributionType
import play.api.libs.json.{ Json, OFormat }

case class ContributionDetail(
  contributionName: String,
  createdOn: String,
  contributionType: ContributionType,
  status: ContributionStatus
)
