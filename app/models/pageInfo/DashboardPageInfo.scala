package models.pageInfo

import models.RecentContribution
import models.UserRole.UserRole

case class DashboardPageInfo(
  logInUserName: String,
  techHubCount: Int,
  blogCount: Int,
  certificationCount: Int,
  openSourceCount: Int,
  blogCountForCurrentMonth: Int,
  techHubTemplateCountForCurrentMonth: Int,
  certificationCountForCurrentMonth: Int,
  openSourceCountForCurrentMonth: Int,
  role: UserRole,
  recentContributions: Seq[RecentContribution]
)
