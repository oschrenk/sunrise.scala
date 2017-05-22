package com.oschrenk.sunrise
import java.time.{ LocalDate, LocalTime, ZoneId }

import org.scalatest._

class SunriseSunsetSpec extends FlatSpec with Matchers {
  val latitude = 52.366667
  val longitude = 4.9
  val zoneId: ZoneId = ZoneId.of("Europe/Amsterdam")
  val date: LocalDate = LocalDate.of(2017, 5, 22)

  "A sunrise" should "happen early in Amsterdam" in {
    val (sunrise, _) = SunriseSunset.of(latitude, longitude, date, zoneId)
    sunrise.toLocalTime shouldBe LocalTime.of(3, 39, 9)
  }

  "A sunset" should "happen late in Amsterdam" in {
    val (_, sunset) = SunriseSunset.of(latitude, longitude, date, zoneId)
    sunset.toLocalTime shouldBe LocalTime.of(19, 37, 34)
  }
}
