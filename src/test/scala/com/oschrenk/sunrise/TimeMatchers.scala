package com.oschrenk.sunrise

import java.time.{Duration, LocalTime}

import org.scalatest._
import matchers._

trait TimeMatchers {

  class LocalTimeWithinTolerance(right: LocalTime, duration: Duration) extends Matcher[java.time.LocalTime] {

    def apply(left: java.time.LocalTime): MatchResult = {
      val leftBound = right.minus(duration)
      val rightBound = right.plus(duration)
      MatchResult(
        left.isAfter(leftBound) && left.isBefore(rightBound),
        s"""LocalTime $left is within $leftBound and $rightBound"""",
        s"""LocalTime $left is out of $leftBound and $rightBound"""",
      )
    }
  }

  def beCloseTo(localTime: LocalTime, tolerance: Duration) = new LocalTimeWithinTolerance(localTime, tolerance)
}

object TimeMatchers extends TimeMatchers
