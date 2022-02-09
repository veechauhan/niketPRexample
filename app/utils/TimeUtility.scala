package utils

import models.TimeZone

import java.time.{ LocalDateTime, ZoneId }

object TimeUtility {

  def getTimestampOfFirstOfCurrentMonth(timeZone: TimeZone): Long =
    LocalDateTime
      .now()
      .withDayOfMonth(1)
      .withMinute(0)
      .withHour(0)
      .withSecond(0)
      .atZone(ZoneId.of(timeZone.toString))
      .toInstant
      .toEpochMilli

}
