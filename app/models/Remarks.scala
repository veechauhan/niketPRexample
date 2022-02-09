package models

import play.api.libs.json.{ Json, OFormat }

case class Remarks(
  id: String,
  remark: String,
  timeStamp: Long
)

object Remarks {
  implicit val remarksFormat: OFormat[Remarks] = Json.format[Remarks]
}
