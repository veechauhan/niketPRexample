package models

import play.api.libs.json.{ Json, OFormat }

case class Remark(
  id: String,
  remark: String,
  timeStamp: Long
)

object Remark {
  implicit val RemarkFormat: OFormat[Remark] = Json.format[Remark]
}
