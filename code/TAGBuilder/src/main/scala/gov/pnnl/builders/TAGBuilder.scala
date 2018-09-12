package gov.pnnl.builders

import org.apache.spark.SparkContext
import gov.pnnl.datamodel
import gov.pnnl.datamodel.TAG
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx.Edge
import org.apache.spark.graphx.Graph
import gov.pnnl.datamodel.GlobalTypes._
import org.apache.spark.graphx.PartitionStrategy


object TAGBuilder {

  /**
    * Builder class to construct an TAGRDD
    * Input file must be of the form
    * 1587	1	5159	1376521889
    * 1410	1	5159	1376521889
    * private method to create TAGRDD
    * @param filepath
    * @param sc
    * @param sep
    * @return a TAGGRDD
    */
  private def init_tagrdd(filepath: String, sc: SparkContext, sep: String = ","): TAGRDD = {

    /*
     * return TAGRDD
     */
    sc.textFile(filepath).map { line =>
      try {
        val fields = line.split(sep)
        val src = fields(0).toInt
        val etype = fields(1).toInt
        val dst = fields(2).toInt
        val time = fields(3).toLong
        val defaultWt = 0.0
        (src, etype, dst, time, defaultWt, Array.empty[Int], Array.empty[Int])
      } catch {
        case ex: java.lang.ArrayIndexOutOfBoundsException => {
          println("AIOB:", line)
          (-1, -1, -1, -1L, 0.0, Array.empty[Int], Array.empty[Int])
        }
        case ex: java.lang.NumberFormatException =>
          println("AIOB2:", line)
          (-1, -1, -1, -1L, 0.0, Array.empty[Int], Array.empty[Int])
      }
    }.distinct
  }

  def init_rdd(filepath: String, sc: SparkContext, sep: String = ","): TAG = {
    val inputTAGRDD = init_tagrdd(filepath, sc, sep)
    new TAG(inputTAGRDD)
  }

  def init_graphx(filepath: String, sc: SparkContext, sep: String = ",",partitionStrategy: PartitionStrategy = null): TAG = {
    val inputTAGRDD: TAGRDD = init_tagrdd(filepath, sc, sep)
    new TAG(graphx_from_tagrdd(inputTAGRDD,partitionStrategy))
  }

 def graphx_from_tagrdd(inputTAGRDD : TAGRDD,partitionStrategy: PartitionStrategy = null) : Graph[vertexId, eType] = {
   val edges = inputTAGRDD.map(quadruple => Edge(quadruple._1, quadruple._3, quadruple._2))
   val vertices = inputTAGRDD.flatMap(triple => Array((triple._1.toLong, triple._1), (triple._3.toLong, triple._3)))
   if(partitionStrategy != null) Graph(vertices,edges).partitionBy(partitionStrategy)
   else Graph(vertices, edges)
 }
  def get_vertexRDD_from_tagrdd(tag_rdd: TAG): RDD[(vertexId, vertexId)] = {
    tag_rdd.get_simple_tagrdd.flatMap(nd => Iterator((nd._1, nd._1), (nd._3, nd._3))).distinct
  }

}  
