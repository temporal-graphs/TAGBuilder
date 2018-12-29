/**
 *
 * @author puro755
 * @dApr 8, 2018
 * @STM
 */




package gov.pnnl.tagbuilder

import org.scalatest._
import gov.pnnl.builders.TAGBuilder
import org.scalactic.source.Position.apply
import gov.pnnl.builders.SparkContextInitializer

/**
 * @author puro755
 *
 */
class Builder extends BuilderTestAbstract {

	  /*val testGSimpleF = "./data/testGSimple.csv"
	  	println("in test class")
    val grdd = TAGBuilder.init_rdd(testGSimpleF, SparkContextInitializer.sc)
    val vRDD = TAGBuilder.get_vertexRDD_from_tagrdd(grdd)

	"A testGSimple Graph" should "have 14 temporal edges" in {
    assert(grdd.tag_rdd.count === 14)
  }
	it should "have 11 vertices" in {
		assert(vRDD.count === 11)
	}
*/
}