
import java.io.File
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.{SaveMode, DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import scala.collection.JavaConversions._
import UDFs._
import org.joda.time.DateTime
import exts.RichConfigExts._

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
  sqlContext.udf.register("bday2age",UDFs.bday2age)
  sqlContext.udf.register("ts2d",UDFs.timestamp2Date)

  val register=config.getConfigList("register")

  register.foreach(cf=>{
    val table=cf.getOptString("table").getOrElse(cf.getString("collection"))
    cf.getOptString("collection").foreach(coll=>{
      sqlContext.read.format("com.stratio.datasource.mongodb")
        .options(mongoConfig.createOptions(coll))
        .load().registerTempTable(table)
    })

    cf.getOptString("sqlStr").foreach(sqlStr=>{
      sqlContext.sql(sqlStr).registerTempTable(table)
    })

  })


  config.getOptStringList("showTables").foreach(tables=>{

    tables.foreach(table=>{
      println(s"table name: $table ")
      sqlContext.sql(s"SELECT * FROM $table").show()
    })

  })
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

  //execute(config.getConfig("execute"))
  config.getConfigList("persistence").foreach(cf=>{
    val coll=cf.getString("collection")
    cf.getStringList("tables").foreach(table=>{
      sqlContext.sql(s"SELECT * FROM $table").write.format("com.stratio.datasource.mongodb").mode(SaveMode.Append)
        .options(mongoConfig.createOptions(coll)).save()
    })
  })
  exit()
}
