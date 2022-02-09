package models

import models.ContributionStatus.ContributionStatus
import models.ContributionType.ContributionType

case class RecentContribution(
  contributionName: String,
  contributionType: ContributionType,
  createdOnDateFormatString: String,
  status: ContributionStatus
)
