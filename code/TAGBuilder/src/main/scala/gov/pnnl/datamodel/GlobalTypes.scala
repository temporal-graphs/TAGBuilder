package gov.pnnl.datamodel

import org.apache.spark.rdd.RDD

object GlobalTypes {

  type vertexId=Int

  type eType=Int
  type eTime=Long
  type eWeight=Double
  type vAttrKey=Array[Int]
  type vAttrKey_VARCHAR=Array[Array[Char]]
  type vAttrVal=Array[Int]
  type vAttrVal_VARCHAR=Array[Array[Char]]

  type TAGRDD = RDD[(vertexId,eType,vertexId,eTime,eWeight,vAttrKey,vAttrVal)]
  type TAGRDD_VARCHAR = RDD[(vertexId,eType,vertexId,eTime,eWeight,vAttrKey_VARCHAR,vAttrVal_VARCHAR)]
  type SimpleTAGRDD = RDD[(vertexId, eType, vertexId, eTime)]


}
