package models

case class MenuActive(
  isDashboardActive: Boolean = false,
  isContributionActive: Boolean = false,
  isNotificationActive: Boolean = false,
  isProfileActive: Boolean = false,
  isStudioUserActive: Boolean = false
)
