package gov.pnnl.builders

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.log4j.Logger
import org.apache.log4j.Level

object SparkContextInitializer {
	

  def getSparkConf(): SparkConf =
  {

    val sparkConf = new SparkConf()
      .set("spark.rdd.compress", "true")
      .set("spark.shuffle.blockTransferService", "nio")
      .set("spark.serializer",
        "org.apache.spark.serializer.KryoSerializer")

    return  sparkConf
  }

  def getSparkContext(givenConf: SparkConf): SparkContext =
  {
    givenConf.registerKryoClasses( Array.empty )
    val sc = new SparkContext( givenConf )
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    return sc
  }

}
