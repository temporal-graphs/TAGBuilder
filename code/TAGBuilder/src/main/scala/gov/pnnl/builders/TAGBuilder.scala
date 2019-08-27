package gov.pnnl.builders

import org.apache.spark.SparkContext
import gov.pnnl.datamodel
import gov.pnnl.datamodel.TAG
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx.Edge
import org.apache.spark.graphx.Graph
import gov.pnnl.datamodel.GlobalTypes._
import org.apache.spark.graphx.PartitionStrategy
import org.apache.spark.sql.SQLContext
import org.graphframes.GraphFrame


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

  def getGraphFrameFromGDF(listof_gdf: Array[String], sep: String, sc: SparkContext): GraphFrame = {

    val gSQLContext = new SQLContext(sc)
    import gSQLContext.implicits._
    val data = sc.textFile(listof_gdf.mkString(",")
                          ).filter(line => (line.startsWith("nodedef") == false) &&
                                           line.startsWith("edgedef") == false).map(line => line.split(sep))
    val v = data.filter(fields => fields.length == 7).map(fields => {
      try {
        getGDFVertexFromFields(fields)
      } catch {
        case ex: java.lang.ArrayIndexOutOfBoundsException => {
          println("AIOB:", fields.mkString(sep))
          (-1, "-1", 1.0, 1.0, 0.0, "NA", false)
        }
        case ex: java.lang.NumberFormatException => {
          println("AIOB2:", fields.mkString(sep))
          (-1, "-1", 1.0, 1.0, 0.0, "NA", false)
        }
      }
    }).distinct().toDF("id", "vtype", "lat", "long", "wt", "label", "inNAI")

    val e = data.filter(fields => fields.length == 6).map(fields => {
      try {
        getGDFEdgeFromFields(fields)
      } catch {
        case ex: java.lang.ArrayIndexOutOfBoundsException => {
          println("AIOB:", fields.mkString(sep))
          (-1, "-1", -1, 1.0, 0.0, "NA")
        }
        case ex: java.lang.NumberFormatException => {
          println("AIOB2:", fields.mkString(sep))
          (-1, "-1", -1, 1.0, 0.0, "NA")
        }
      }
    }).distinct().toDF("src", "etype", "dst", "time", "wt", "label")

    GraphFrame(v, e)
  }

  def getGDFEdgeFromFields(fields:Array[String]): (Int,String,Int,Double,Double,String) =
  {
    val src :Int= fields(0).toInt
    val etype :String= fields(1)
    val dst :Int= fields(2).toInt
    val time :Double = fields(3).toDouble
    val wt :Double= fields(4).toDouble
    val label :String= fields(5)
    (src,etype,dst,time,wt,label)
  }
  def getGDFVertexFromFields(fields: Array[String]): (Int,String,Double,Double,Double,String,Boolean) =
  {
    val id :Int= fields(0).toInt
    val vtype :String = fields(1)
    val lat :Double= fields(2).toDouble
    val long :Double = fields(3).toDouble
    val wt :Double = if(fields(4).equalsIgnoreCase("na") == false) fields(4).toDouble else Double.NaN
    val label :String= fields(5)
    var inNAI :Boolean= false
    if (fields.length > 6)
    {
      if(fields(6).equals("0") || fields(6).equalsIgnoreCase("false"))
        inNAI = false
      else inNAI = true
    }
    (id, vtype, lat,long,wt, label, inNAI)
  }
  def getGraphFrameFromGDF(gdf_v_filepath :String, gdf_e_filepath :String,sep:String, sc:SparkContext): GraphFrame = {
    /*
     * Read node def file
     */
      val gSQLContext = new SQLContext(sc)
      import gSQLContext.implicits._
      val v = sc.textFile(gdf_v_filepath).filter(line => line.startsWith("nodedef") == false).map { line =>
        try {
          getGDFVertexFromFields(line.split(sep) )
        } catch {
          case ex: java.lang.ArrayIndexOutOfBoundsException => {
            println("AIOB:", line)
            (-1, "-1", 1.0,1.0,  0.0, "NA", false)
          }
          case ex: java.lang.NumberFormatException => {
            println("AIOB2:", line)
            (-1, "-1", 1.0,1.0,  0.0, "NA", false)
          }}
      }.distinct.toDF("id","vtype","lat","long","wt","label","inNAI")

    val e = sc.textFile(gdf_e_filepath).filter(line => line.startsWith("edgedef") == false).map { line =>
      try {
        val fields = line.split(sep)
        getGDFEdgeFromFields(fields)
      } catch {
        case ex: java.lang.ArrayIndexOutOfBoundsException => {
          println("AIOB:", line)
          (-1, "-1",-1, 1.0,0.0, "NA")
        }
        case ex: java.lang.NumberFormatException => {
          println("AIOB2:", line)
          (-1, "-1", -1,1.0,0.0, "NA")
      }}
    }.distinct.toDF("src","etype","dst","time","wt","label")

    GraphFrame(v,e)
  }
}  
