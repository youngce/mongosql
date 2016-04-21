/**
  * Created by mark on 2016/4/14.
  */

import java.util

import com.stratio.datasource.mongodb.config.MongodbConfig._
import com.typesafe.config.Config
import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.mutable

class MongoConfig(config: Config){
  private val map=mapAsScalaMap(config.getAnyRef("mongo").asInstanceOf[util.HashMap[String,String]])

  def createOptions(collection:String): mutable.Map[String, String] = map.+(Collection->collection)

}
