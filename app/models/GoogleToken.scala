package models

import play.api.libs.json.{ Json, OFormat }

case class GoogleToken(
  sub: String,
  email: String,
  name: String,
  picture: String
)

object GoogleToken {
  implicit val GoogleTokenFormat: OFormat[GoogleToken] = Json.format[GoogleToken]
}
