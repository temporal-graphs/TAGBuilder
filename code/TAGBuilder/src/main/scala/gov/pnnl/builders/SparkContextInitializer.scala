package gov.pnnl.builders

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.log4j.Logger
import org.apache.log4j.Level

object SparkContextInitializer {
	
	
  val sparkConf = new SparkConf()
    .setAppName("DARPA-MAA SDGG Graph Generation")
    .set("spark.rdd.compress", "true")
    .set("spark.shuffle.blockTransferService", "nio")
    .set("spark.serializer",
      "org.apache.spark.serializer.KryoSerializer")
    .setMaster("local")


  sparkConf.registerKryoClasses( Array.empty )
  val sc = new SparkContext( sparkConf )
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

}
