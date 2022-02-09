package models

case class Studio(
  id: Option[String],
  studioName: String,
  studioManagerId: Option[String],
  disabledTimestamp: Long
)
