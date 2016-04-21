

name := "mongoSQL"

version := "0.1.2"

scalaVersion := "2.10.4"

//libraryDependencies += "org.mongodb" % "casbah-commons_2.10" % "2.8.0"
libraryDependencies += "com.stratio.datasource" % "spark-mongodb_2.10" % "0.11.1"
//libraryDependencies += "com.stratio" % "spark-mongodb" % "0.8.7"

libraryDependencies += ("org.apache.spark" % "spark-sql_2.10" % "1.5.2")
libraryDependencies += "com.typesafe" % "config" % "1.3.0"
mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x => old(x)
}
}
