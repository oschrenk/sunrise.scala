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
  val oneMicroSecond: Duration = Duration.ofNanos(1000)

  "A sunrise" should "happen early in Amsterdam" in {
    val (sunrise, _) = SunriseSunset.of(latitude, longitude, date, zoneId)
    sunrise.get.toLocalTime should beCloseTo(LocalTime.of(5, 39), oneMinuteTolerance)
  }

  it should "happen early in Amsterdam even in winter" in {
    val (sunrise, _) = SunriseSunset.of(latitude, longitude, winterDate, zoneId)
    sunrise.get.toLocalTime should beCloseTo(LocalTime.of(7, 48), oneMinuteTolerance)
  }

  "The sun" should "always be up in Kresty in summer" in {
    val lat = 71.8333
    val lon = 102.4833
    val date = LocalDate.of(2017, 5, 22)
    val zoneId = ZoneId.of("Asia/Krasnoyarsk")
    val (sunrise, sunset) = SunriseSunset.of(lat, lon, date, zoneId)
    sunrise.get.toLocalTime should beCloseTo(LocalTime.of(7, 0), oneMicroSecond)
    sunset.isDefined shouldBe false
  }

  it should "always be down in Kresty in winter" in {
    val lat = 71.8333
    val lon = 102.4833
    val date = LocalDate.of(2017, 11, 22)
    val zoneId = ZoneId.of("Asia/Krasnoyarsk")
    val (sunrise, sunset) = SunriseSunset.of(lat, lon, date, zoneId)
    sunrise.isDefined shouldBe false
    sunset.get.toLocalTime should beCloseTo(LocalTime.of(7, 0), oneMicroSecond)
  }

  "A sunset" should "happen late in Amsterdam" in {
    val (_, sunset) = SunriseSunset.of(latitude, longitude, date, zoneId)
    sunset.get.toLocalTime should beCloseTo(LocalTime.of(21, 37), oneMinuteTolerance)
  }
}
