package models

import models.UserRole.UserRole

case class StudioUserPageInfo(
  loggedInUserName: String,
  loggedInUserRole: UserRole,
  listOfStudioUsers: Seq[StudioUser],
  pageNumber: Int
)
