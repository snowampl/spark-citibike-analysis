
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object Bikeshare {
  def main(args: Array[String]) = {
    
    val conf = new SparkConf()
      .setAppName("Bikeshare Analysis")

    val sc = new SparkContext(conf)
    
    val nums = sc.makeRDD(Array(1,2,3))
    nums.map(_*2).collect().foreach(println(_))
  }
}
