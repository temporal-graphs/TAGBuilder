package gov.pnnl.driver

import gov.pnnl.builders.SparkContextInitializer
import gov.pnnl.builders.TAGBuilder

object TestTAGBuilder {

  def main(args: Array[String]): Unit = {

    val sparkConf   = SparkContextInitializer.getSparkConf().setAppName("Test Builder").setMaster("local")
    val gSC         = SparkContextInitializer.getSparkContext(sparkConf)
    val filepath = "/Users/puro755/OneDrive - PNNL/Projects/DARPA_MAA/Data/gradient/tmp/intGraph/allPhoneEmailWithEType20k.csv"
    val g = TAGBuilder.init_rdd(filepath,gSC,"\t")
    print(g.tag_rdd.count)
    println("done")


    /*
    val sparkConf   = SparkContextInitializer.getSparkConf().setAppName("Sigma+ MotifFiner").setMaster("local")
    val gSC         = SparkContextInitializer.getSparkContext(sparkConf)

    val v_file = "/Users/puro755/OneDrive - PNNL/Projects/DARPA_MAA/SigmaPlus/data/RNsubgraphs_v.csv"
    val e_file = "/Users/puro755/OneDrive - PNNL/Projects/DARPA_MAA/SigmaPlus/data/RNsubgraphs_e.csv"
    val g = TAGBuilder.getGraphFrameFromGDF(v_file,e_file,",",gSC)
    g.edges.show()
    */

  }

}
