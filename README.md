# Sunrise

Calculating the sunrise and sunset in Scala following the [Sunrise equation](https://en.wikipedia.org/wiki/Sunrise_equation).

## Usage

**Dependencies**

```
resolvers += Resolver.bintrayRepo("oschrenk", "maven")
libraryDependencies += "com.oschrenk" %% "sunrise-scala" % "0.1.0"
```

**Your application**

```
import com.oschrenk.sunrise.SunriseSunset

// Amsterdam
val latitude = 52.366667
val longitude = 4.9
val zoneId: ZoneId = ZoneId.of("Europe/Amsterdam")
val date: LocalDate = LocalDate.of(2017, 5, 22)

val (sunrise, sunset) = SunriseSunset.of(latitude, longitude, date, zoneId)
println(sunrise) // 2017-05-22T03:39:09+02:00[Europe/Amsterdam]
println(sunset) // 2017-05-22T19:37:34+02:00[Europe/Amsterdam]
```

