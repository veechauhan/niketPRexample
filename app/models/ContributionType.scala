package models

import play.api.libs.json.{ Format, Json }

object ContributionType extends Enumeration {

  type ContributionType = Value

  val TechHubTemplate, Blog, Certification, OpenSource = Value

  implicit val ContributionTypeFormat: Format[models.ContributionType.Value] = Json.formatEnum(this)

}
