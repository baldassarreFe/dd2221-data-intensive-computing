package lab3.problems

import java.util.{Calendar, TimeZone}

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide
import com.dataartisans.flinktraining.exercises.datastream_java.sources.TaxiRideSource
import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector

import scala.collection.mutable

object JfkTerminals {
  private val extractLatLon = (ride: TaxiRide) =>
    if (ride.isStart)
      (ride.startLon, ride.startLat)
    else
      (ride.endLon, ride.endLat)

  private val extractTerminal = (lon: Float, lat: Float) =>
    TerminalUtils.gridToTerminal(GeoUtils.mapToGridCell(lon, lat))

  def main(args: Array[String]) {
    val maxDelay = 60 // events are out of order by max 60 seconds
    val speed = 600 // events of 10 minutes are served in 1 second

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    val rides = env.addSource(new TaxiRideSource("nycTaxiRides.gz", maxDelay, speed))

    val jfkRides = rides
      .map(extractLatLon)
      .map(extractTerminal.tupled)
      .filter(_ != Terminal_404)

    val windowedCountsByTerminal = jfkRides
      .keyBy(terminal => terminal)
      .window(TumblingEventTimeWindows.of(Time.hours(1)))
      .apply((terminal: Terminal, window, records, out: Collector[(Terminal, Int, Int)]) => {
        val cal: Calendar = Calendar.getInstance()
        cal.setTimeZone(TimeZone.getTimeZone("America/New_York"))
        cal.setTimeInMillis(window.getStart)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        out.collect((terminal, records.size, hour))
      })
    windowedCountsByTerminal.print()

    val windowedBusiestTerminal = jfkRides
      .timeWindowAll(Time.hours(1))
      .apply((window, records, out: Collector[(Terminal, Int, Int)]) => {
        val cal: Calendar = Calendar.getInstance()
        cal.setTimeZone(TimeZone.getTimeZone("America/New_York"))
        cal.setTimeInMillis(window.getStart)
        val hour = cal.get(Calendar.HOUR_OF_DAY)

        val counts = records
          .foldLeft(mutable.HashMap.empty[Terminal, Int].withDefaultValue(0))((acc, terminal) => {
            acc(terminal) += 1
            acc
          })
        val busiestTerminal = counts.maxBy(_._2)
        out.collect((busiestTerminal._1, busiestTerminal._2, hour))
      })
    windowedBusiestTerminal.print()

    // execute program
    env.execute("Airport Terminals")
  }
}