package gov.pnnl.datamodel
import gov.pnnl.builders
import gov.pnnl.datamodel.GlobalTypes._
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Graph

class TAG(val tag_rdd: TAGRDD, val tag_graph : Graph[vertexId, eType] ) {

	def this(tag_rdd: TAGRDD)
	{
		this(tag_rdd, null)
	}
	
	def this(tag_graph : Graph[vertexId, eType])
	{
		this(null,tag_graph)
	}

  def get_simple_tagrdd() : SimpleTAGRDD  =
  {
  	tag_rdd.map(e => (e._1, e._2, e._3, e._4))
  }

}
