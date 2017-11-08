package com.oschrenk.sunrise
import java.time.{ Duration, LocalDate, LocalTime, ZoneId }

import org.scalatest._
import TimeMatchers._

class SunriseSunsetSpec extends FlatSpec with Matchers {
  val latitude = 52.366667
  val longitude = 4.9
  val zoneId: ZoneId = ZoneId.of("Europe/Amsterdam")
  val date: LocalDate = LocalDate.of(2017, 5, 22)
  val winterDate: LocalDate = LocalDate.of(2017, 11, 8)
  val oneMinuteTolerance: Duration = Duration.ofMinutes(1)

  "A sunrise" should "happen early in Amsterdam" in {
    val (sunrise, _) = SunriseSunset.of(latitude, longitude, date, zoneId)
    sunrise.toLocalTime should beCloseTo(LocalTime.of(5, 39), oneMinuteTolerance)
  }

  it should "happen early in Amsterdam even in winter" in {
    val (sunrise, _) = SunriseSunset.of(latitude, longitude, winterDate, zoneId)
    sunrise.toLocalTime should beCloseTo(LocalTime.of(7, 48), oneMinuteTolerance)
  }

  "A sunset" should "happen late in Amsterdam" in {
    val (_, sunset) = SunriseSunset.of(latitude, longitude, date, zoneId)
    sunset.toLocalTime should beCloseTo(LocalTime.of(21, 37), oneMinuteTolerance)
  }
}
