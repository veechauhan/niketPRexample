package models

import models.UserRole.UserRole
import play.api.libs.json.{ Json, OFormat }

case class ContributionPageInfo(
  loggedInUserName: String,
  loggedInUserRole: UserRole,
  contributionDetail: Seq[ContributionDetail],
  pageNumber: Int
)
