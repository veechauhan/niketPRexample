package models

case class Project(
  id: Option[String],
  projectName: String,
  projectInfo: String,
  studioId: String,
  disabledTimestamp: Long
)
