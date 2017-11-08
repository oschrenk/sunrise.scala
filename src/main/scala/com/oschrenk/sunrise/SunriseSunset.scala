package com.oschrenk.sunrise

import java.time._
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.JulianFields

import scala.math.{ cos, sin, toRadians }

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

  private def toZonedDate(julianDay: BigDecimal, zoneId: ZoneId): ZonedDateTime = {
    val days = Math.floor(julianDay.toDouble).toInt
    val fraction = julianDay - days
    val day = LocalDate.from(JulianDayFormatter.parse(days.toString))
    val time = LocalTime.ofSecondOfDay((SecondsInDay * fraction).toLong)
    val unzonedTime = day.atTime(time)
    val offset = zoneId.getRules.getOffset(unzonedTime)
    println(offset.getTotalSeconds)
    unzonedTime.atZone(zoneId).plusSeconds(offset.getTotalSeconds)
  }

  def of(latitude: Double, longitude: Double, date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): (ZonedDateTime, ZonedDateTime) = {
    val nStar = date.getLong(JulianFields.JULIAN_DAY) - J2000 + Drift - longitude / 360

    val m0 = BigDecimal("357.5291") // in degrees
    val m1 = BigDecimal("0.98560028") // in degrees per day
    val meanAnomaly = (m0 + m1 * nStar) % 360
    val meanAnomalyRadians = toRadians(meanAnomaly.toDouble)

    val equationOfCenter =
      BigDecimal("1.9148") * sin(meanAnomalyRadians) +
        BigDecimal("0.0200") * sin(2 * meanAnomalyRadians) +
        BigDecimal("0.0003") * sin(3 * meanAnomalyRadians)
    val eclipticalLongitude = (meanAnomaly + equationOfCenter + PerhilionEarth + 180) % 360
    //  The coefficients are fractional day minutes.
    val simplifiedEquationOfTime =
      BigDecimal("0.0053") * sin(meanAnomalyRadians.toDouble) +
        BigDecimal("-0.0069") * sin(2 * toRadians(eclipticalLongitude.toDouble))
    // 0.5 is midnight
    val jTransit = J2000 + 0.5 + nStar + simplifiedEquationOfTime

    val sunDeclination = sin(toRadians(eclipticalLongitude.toDouble)) * sin(toRadians(EarthObliquity.toDouble))
    val observerCorrection = sin(toRadians(BigDecimal("-0.83").toDouble))
    val hourAngle = Math.acos(
      (observerCorrection - sin(latitude.toRadians) * sin(sunDeclination)) /
        (cos(latitude.toRadians) * cos(sunDeclination))
    ).toDegrees / 360

    val sunrise = jTransit - hourAngle
    val sunset = jTransit + hourAngle

    (toZonedDate(sunrise, zoneId), toZonedDate(sunset, zoneId))
  }
}
