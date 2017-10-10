package lab3.practice

/**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

import com.dataartisans.flinktraining.exercises.datastream_java.sources.TaxiRideSource
import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector

/**
  * Skeleton for a Flink Streaming Job.
  *
  * For a full example of a Flink Streaming Job, see the SocketTextStreamWordCount.java
  * file in the same package/directory or have a look at the website.
  *
  * You can also generate a .jar file that you can submit on your Flink
  * cluster. Just type
  * {{{
  *   mvn clean package
  * }}}
  * in the projects root directory. You will find the jar in
  * target/flink-scala-project-0.1.jar
  * From the CLI you can then run
  * {{{
  *    ./bin/flink run -c org.apache.flink.quickstart.StreamingJob target/flink-scala-project-0.1.jar
  * }}}
  *
  * For more information on the CLI see:
  *
  * http://flink.apache.org/docs/latest/apis/cli.html
  */
object FindPopularPlaces {


  def main(args: Array[String]) {
    val maxDelay = 60 // events are out of order by max 60 seconds
    val speed = 600 // events of 10 minutes are served in 1 second
    val popularityThreshold = 30 // locations with more than 30 starts or 30 ends

    // get an ExecutionEnvironment
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // configure event-time processing
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    // get the taxi ride data stream
    val rides = env.addSource(
      new TaxiRideSource("nycTaxiRides.gz", maxDelay, speed))

    val nyRides = rides
      .filter(tr => GeoUtils.isInNYC(tr.startLon, tr.startLat))
      .filter(tr => GeoUtils.isInNYC(tr.endLon, tr.endLat))

    val windowedNyRides = nyRides
      .map(tr => if (tr.isStart) (tr.startLon, tr.startLat, true) else (tr.endLon, tr.endLat, false))
      .map(tup => (GeoUtils.mapToGridCell(tup._1, tup._2), tup._3))
      .keyBy(tup => tup)
      .window(SlidingEventTimeWindows.of(Time.minutes(15), Time.minutes(5)))

    val countsByWindow = windowedNyRides
      .apply((key: (Int, Boolean), window, records, out: Collector[(Int, Long, Boolean, Int)]) => {
        out.collect((key._1, window.getEnd, key._2, records.size))
      })

    val popularLocations = countsByWindow
      .filter(count => count._4 > popularityThreshold)
      .map(tup =>
        (GeoUtils.getGridCellCenterLon(tup._1), GeoUtils.getGridCellCenterLat(tup._1), tup._2, tup._3, tup._4))

    popularLocations.print()

    // execute program
    env.execute("Find Popular Places")
  }
}