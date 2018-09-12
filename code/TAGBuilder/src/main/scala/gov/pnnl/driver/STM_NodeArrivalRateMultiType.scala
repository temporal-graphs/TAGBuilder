package gov.pnnl.driver

import gov.pnnl.builders.SparkContextInitializer
import gov.pnnl.builders.TAGBuilder

object STM_NodeArrivalRateMultiType {

  def main(args: Array[String]): Unit = {

    val sc = SparkContextInitializer.sc
    val filepath = "/Users/puro755/OneDrive - PNNL/Projects/DARPA_MAA/Data/gradient/tmp/intGraph/allPhoneEmailWithEType20k.csv"
    val g = TAGBuilder.init_rdd(filepath,sc)
    print(g.tag_rdd.count)
    println("done")
  }

}
