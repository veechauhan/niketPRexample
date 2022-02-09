package models

trait TimeZone

object TimeZone {

  case object IST extends TimeZone {
    override def toString: String = "Asia/Kolkata"
  }
}
