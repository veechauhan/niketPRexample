package models

object UserRole extends Enumeration {

  type UserRole = Value

  val USER, STUDIO_ADMIN, STUDIO_MANAGER, SITE_MANAGER = Value

}
