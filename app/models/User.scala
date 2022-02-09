package models

import models.UserRole.UserRole

case class User(
  id: Option[String],
  name: String,
  email: String,
  bio: Option[String] = None,
  mobileNumber: Option[String] = None,
  managerId: Option[String] = None,
  githubId: Option[String] = None,
  role: UserRole = UserRole.USER,
  projectId: Option[String] = None,
  studioId: Option[String] = None,
  disabledTimestamp: Long = 0L
)
