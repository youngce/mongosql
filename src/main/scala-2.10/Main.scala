
import java.io.File
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.{SaveMode, DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import scala.collection.JavaConversions._
import UDFs._
import org.joda.time.DateTime


object Main extends App{


  implicit val config: Config = ConfigFactory.parseFile(new File(args(0)))
  //println("The answer is: " + config.getString("simple-app.answer"))

  val mongoConfig=new MongoConfig(config)
  //val option=envConf.getAnyRef("mongo").asInstanceOf[util.HashMap]
  val conf=new SparkConf().setAppName("anta")
    .setMaster(config.getString("spark.master"))
    .set("spark.executor.memory",config.getString("spark.mem"))

  val sc=new SparkContext(conf)
  implicit val sqlContext=new SQLContext(sc)


  def getCollectionDF(collection:String)={

    sqlContext.read.format("com.stratio.datasource.mongodb").options(mongoConfig.createOptions(collection)).load()

      //.registerTempTable(collection)
  }
  sqlContext.udf.register("bday2age",UDFs.bday2age)
  sqlContext.udf.register("dt2d",UDFs.dt2d)

  def toJoin(joinConfig:Config)(implicit sqlContext: SQLContext): Unit ={


    def getDF(collectionConfig:Config)={

      val df= sqlContext.read.format("com.stratio.datasource.mongodb")
        .options(mongoConfig.createOptions(collectionConfig.getString("name"))).load()
      val selectCols=collectionConfig.getStringList("selectCols").map(df(_))
      df.select(selectCols:_*)

      //getCollectionDF(collectionConfig.getString("name"))//.select(sel)
    }
    val coll1=getDF(joinConfig.getConfig("collection1"))
    val coll2=getDF(joinConfig.getConfig("collection2"))
    coll1.join(coll2,
      coll1(joinConfig.getString("collection1.joinCol"))===coll2(joinConfig.getString("collection2.joinCol")))
      .registerTempTable(joinConfig.getString("table"))
  }


  toJoin(config.getConfig("join"))
  def execute(executeConfig:Config)(implicit sqlContext: SQLContext): Unit ={
    val sqlStrs=executeConfig.getStringList("sql")
    val isShow=executeConfig.getBoolean("isShow")
    val saveConf=executeConfig.getConfig("save")
    sqlStrs.toList.foreach(sqlStr=>{
      val df=sqlContext.sql(sqlStr.replace("\\n",""))
      if (isShow) df.show()
      if (saveConf.getBoolean("isSave")) {
        df.write.format("com.stratio.datasource.mongodb").mode(SaveMode.Append)
          .options(mongoConfig.createOptions(saveConf.getString("collection"))).save()

      }
    })

    //
  }
  execute(config.getConfig("execute"))

  exit()
}
