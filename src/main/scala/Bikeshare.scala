
import scala.Array.canBuildFrom

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.serializer.KryoRegistrator
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import com.esotericsoftware.kryo.Kryo

object Bikeshare {

  val tripsFile = "s3n://citibike-tripdata/citibike_tripdata_2013-07_2014-02.csv.raw"

  def main(args: Array[String]) = {

    val conf = new SparkConf()
      .setAppName("Example App")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kry.registrator", "BikeshareRegistrator")
      .set("fs.s3n.awsAccessKeyId", sys.env.get("AWS_ACCESS_KEY_ID").get)
      .set("fs.s3n.awsSecretAccessKey", sys.env.get("AWS_SECRET_ACCESS_KEY").get)
    implicit val sc = new SparkContext(conf)
    
    val tripsRaw = sc.textFile(tripsFile).cache()
    val trips = parseTripsCsv(tripsRaw)

    val stationIds = trips.flatMap(t => Seq(t.startStation.id, t.endStation.id)).distinct
    println(stationIds.count)
  }
  
  def parseTripsCsv(tripsRaw: RDD[String])(implicit sc: SparkContext) = {
    import Types._
    val format = sc.broadcast(DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss"))
    val trips = tripsRaw
      .map { line: String =>
        line
          // split the csv line
          .split(",")
          // remove leading and trailing quote characters from each entry
          .map(_.stripPrefix("\"").stripSuffix("\""))
      }
      .map { line: Array[String] =>
        val startStation = Station(
          line(3).toInt,
          line(4),
          line(5).toDouble,
          line(6).toDouble)
        val endStation = Station(
          line(7).toInt,
          line(8),
          line(9).toDouble,
          line(10).toDouble)

        Trip(
          line(0).toInt,
          format.value.parseDateTime(line(1)),
          format.value.parseDateTime(line(2)),
          startStation,
          endStation,
          line(11).toInt,
          line(12),
          if (line(12) == "Subscriber") line(13).toInt else -1,
          line(14).toInt)
      }
    trips
  }
}

object BikeshareRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) {
    import Types._
    kryo.register(classOf[Station])
    kryo.register(classOf[Trip])
    kryo.register(classOf[DateTimeFormat])
  }
}

object Types {
  case class Station(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double)

  case class Trip(
    val tripDuration: Int,
    val startTime: DateTime,
    val stopTime: DateTime,
    val startStation: Station,
    val endStation: Station,
    val bikeId: Int,
    val userType: String,
    val birthYear: Int,
    val gender: Int)
}
