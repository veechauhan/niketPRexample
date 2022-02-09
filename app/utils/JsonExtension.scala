package utils

import play.api.libs.json.{ JsValue, Json, Reads }

import scala.language.implicitConversions
import scala.util.Try

object JsonExtension {

  implicit def stringToJson(jsonString: String): JsValue = Json.parse(jsonString)

  implicit class JsonOps(json: String) {

    def fromJson[A](implicit readsA: Reads[A]): Option[A] =
      Try(Json.fromJson[A](json).get).toOption
  }

}
