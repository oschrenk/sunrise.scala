# Sunrise

Calculating the sunrise and sunset in Scala following the [Sunrise equation](https://en.wikipedia.org/wiki/Sunrise_equation).

*Edge cases*
Since places within the polar circles might experience [Midnight Sun](https://en.wikipedia.org/wiki/Midnight_sun) or
 [Polar Night](https://en.wikipedia.org/wiki/Polar_night), sunrise or sunset are actually optional for certain places.

That is why the library returns an option, as seen in the example below.

## Usage

**Dependencies**

```
resolvers += Resolver.bintrayRepo("oschrenk", "maven")
libraryDependencies += "com.oschrenk.spacetime" %% "sunrise-scala" % "0.3.0"
```

**Your application**

```
import com.oschrenk.spacetime.sunrise.SunriseSunset

// Amsterdam
val latitude = 52.366667
val longitude = 4.9
val zoneId: ZoneId = ZoneId.of("Europe/Amsterdam")
val date: LocalDate = LocalDate.of(2017, 5, 22)

SunriseSunset.of(latitude, longitude, date, zoneId) match {
  case Right((sunrise,sunset)) =>
    println(sunrise) // 2017-05-22T05:39:09+02:00[Europe/Amsterdam]
    println(sunset)  // 2017-05-22T21:37:34+02:00[Europe/Amsterdam]
  case Left(PolarDay(_)) =>
    println("Polar day")
  case Left(PolarNight(_)) =>
    println("Polar night")
```

Either[Polar, (sunrise, sunset)]


## Publish

```
# publish cross-compiled versions
sbt +publish
```
