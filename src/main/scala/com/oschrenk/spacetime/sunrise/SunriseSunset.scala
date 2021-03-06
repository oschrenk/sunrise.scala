package com.oschrenk.spacetime.sunrise

import java.time._
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.JulianFields

import scala.math.{cos, sin, toRadians}

sealed trait SunriseSunset
case object PolarDay extends SunriseSunset
case object PolarNight extends SunriseSunset
case class Day(sunrise: ZonedDateTime, sunset: ZonedDateTime)
  extends SunriseSunset

object SunriseSunset {
  private val J2000 = LocalDate.of(2000, 1, 1).getLong(JulianFields.JULIAN_DAY)
  // correct for leap seconds and terrestrial time
  private val Drift = 68.184 / 86400

  private val PerhilionEarth = BigDecimal("102.9372")
  private val EarthObliquity = BigDecimal("23.44")

  private val UTC = ZoneOffset.UTC
  private val SecondsInDay = 24 * 60 * 60
  private val JulianDayFormatter = new DateTimeFormatterBuilder()
    .appendValue(JulianFields.JULIAN_DAY)
    .toFormatter().withZone(UTC)

  // mean anomaly at a fixed moment in degrees
  private val m0 = BigDecimal("357.5291")
  // the rate of change of the mean anomaly in degrees per day
  private val m1 = BigDecimal("0.98560028")

  // equation of center coefficients
  private val C1 = BigDecimal("1.9148")
  private val C2 = BigDecimal("0.0200")
  private val C3 = BigDecimal("0.0003")

  // simplified equation of time coefficients in fractional day minutes.
  private val D1 = BigDecimal("0.0053")
  private val D2 = BigDecimal("-0.0069")

  private val altitudeSolarDiscInDegrees = BigDecimal("-0.83")
  // we assume sea level for observer
  private val elevationInMeter = 0
  private val observerCorrection = BigDecimal("-2.076") * Math.sqrt(elevationInMeter) / 60
  private val sinOfAltitudeSolarDisc = sin(toRadians((altitudeSolarDiscInDegrees + observerCorrection).toDouble))

  private def normal(julianDay: BigDecimal, zoneId: ZoneId): ZonedDateTime = {
    val days = Math.floor(julianDay.toDouble).toInt
    val day = LocalDate.from(JulianDayFormatter.parse(days.toString))

    val fraction = julianDay - days
    val time = LocalTime.ofSecondOfDay((SecondsInDay * fraction).toLong)
    toZoneDateTime(day, time, zoneId)
  }

  private def toZoneDateTime(day: LocalDate, time: LocalTime, zoneId: ZoneId): ZonedDateTime = {
    val unzonedTime = day.atTime(time)
    val offset = zoneId.getRules.getOffset(unzonedTime)
    unzonedTime.atZone(zoneId).plusSeconds(offset.getTotalSeconds)
  }

  def of(latitude: Double,
         longitude: Double,
         date: LocalDate,
         zoneId: ZoneId = ZoneId.systemDefault()): SunriseSunset = {
    val nStar = date.getLong(JulianFields.JULIAN_DAY) - J2000 + Drift - longitude / 360

    val meanAnomaly = (m0 + m1 * nStar) % 360
    val meanAnomalyRadians = toRadians(meanAnomaly.toDouble)

    val equationOfCenter =
      C1 * sin(meanAnomalyRadians) +
        C2 * sin(2 * meanAnomalyRadians) +
        C3 * sin(3 * meanAnomalyRadians)
    val eclipticalLongitude = (meanAnomaly + equationOfCenter + PerhilionEarth + 180) % 360

    val simplifiedEquationOfTime =
      D1 * sin(meanAnomalyRadians.toDouble) +
        D2 * sin(2 * toRadians(eclipticalLongitude.toDouble))

    // 0.5 is midnight
    val jTransit = J2000 + 0.5 + nStar + simplifiedEquationOfTime

    val sunDeclination = sin(toRadians(eclipticalLongitude.toDouble)) * sin(toRadians(EarthObliquity.toDouble))
    val operand = (sinOfAltitudeSolarDisc - sin(latitude.toRadians) * sin(sunDeclination)) /
        (cos(latitude.toRadians) * cos(sunDeclination))
    if (operand < -1)
      PolarDay
    else if (operand > 1)
      PolarNight
    else {
      val hourAngle = Math.acos(operand).toDegrees / 360

      val sunrise = jTransit - hourAngle
      val sunset = jTransit + hourAngle

      Day(normal(sunrise, zoneId), normal(sunset, zoneId))
    }
  }

}
