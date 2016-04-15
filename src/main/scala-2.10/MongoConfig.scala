/**
  * Created by mark on 2016/4/14.
  */

import java.util

import com.stratio.datasource.mongodb.config.MongodbConfig._
import com.typesafe.config.Config
import scala.collection.JavaConversions.mapAsScalaMap
class MongoConfig(config: Config){
  private val map=mapAsScalaMap(config.getAnyRef("mongo").asInstanceOf[util.HashMap[String,String]])

  def createOptions(collection:String)= map.+(Collection->collection)

}
