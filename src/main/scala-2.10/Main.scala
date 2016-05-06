
import java.io.File
import java.sql.Timestamp


import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.{SaveMode, DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import scala.collection.JavaConversions._
import UDFs._
import org.joda.time.DateTime
import exts.RichConfigExts._

object Main extends App{
//  bday2age
//
  val ts=new Timestamp(397670400000.0.toLong)
  val res=bday2age(ts)
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

  val registerConfs=config.getConfigList("register")

  registerConfs.foreach(cf=>{

    val table=cf.getOptString("table").getOrElse(cf.getString("collection"))
    def registerTableFromConfig(path:String)(func:String=>DataFrame)={
      cf.getOptString(path).foreach(str=>func(str).registerTempTable(table))
    }
    registerTableFromConfig("collection")(coll=>{
      sqlContext.read.format("com.stratio.datasource.mongodb")
                .options(mongoConfig.createOptions(coll)).load()
    })

    registerTableFromConfig("sqlStr")(sqlStr=>{
      sqlContext.sql(sqlStr)
    })

  })


  config.getOptStringList("showTables").foreach(tables=>{

    tables.foreach(table=>{
      println(s"table name: $table ")
      sqlContext.sql(s"SELECT * FROM $table").show()
    })

  })

  config.getConfigList("persistence").foreach(cf=>{
    val coll=cf.getString("collection")
    cf.getStringList("tables").foreach(table=>{
      sqlContext.sql(s"SELECT * FROM $table").write.format("com.stratio.datasource.mongodb").mode(SaveMode.Append)
        .options(mongoConfig.createOptions(coll)).save()
    })
  })
  exit()
}
