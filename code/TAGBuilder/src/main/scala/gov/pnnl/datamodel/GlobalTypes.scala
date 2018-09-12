package gov.pnnl.datamodel

import org.apache.spark.rdd.RDD

object GlobalTypes {

  type vertexId=Int
  type eType=Int
  type eTime=Long
  type eWeight=Double
  type vAttrKey=Array[Int]
  type vAttrVal=Array[Int]

  type TAGRDD = RDD[(vertexId,eType,vertexId,eTime,eWeight,vAttrKey,vAttrVal)]
  type SimpleTAGRDD = RDD[(vertexId, eType, vertexId, eTime)]


}
