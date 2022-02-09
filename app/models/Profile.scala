package models

import models.UserRole.UserRole

case class Profile(
  name: String,
  email: String,
  studio: String,
  bio: String,
  mobile: String,
  userRole: UserRole
)
