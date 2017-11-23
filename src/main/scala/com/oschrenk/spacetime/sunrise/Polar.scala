package com.oschrenk.spacetime.sunrise

import java.time.LocalDate

sealed trait Polar {
  val on: LocalDate
}
case class PolarDay(on: LocalDate) extends Polar
case class PolarNight(on: LocalDate) extends Polar

