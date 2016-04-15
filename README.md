###Config Setting

#Step1 Set your config
```
spark {
  master="local[*]"
  mem="4g" //
}
//mongoDB config
mongo{
  host = "monogDB host",
  database = "db name",
  credentials = "username,dbname,password",
  schema_samplingRatio = "1.0"
}
join{
  table="join table name"
  collection1{
    name="mongodb collection1 name"
    joinCol="join col name"
    selectCols=[col1,col2,...]

  }
  collection2{
    name="mongodb collection2 name"
    joinCol="join col name"
    selectCols=[col1,col2,...]
  }
}
execute{
  sql=[sql cmd1, sql cmd2, ... ]
  isShow=false
  save{
    isSave=true
    collection="save collection name"
  }

}

```
#Step2 package jar
```
sbt assembly
```
#Step3 Run
```
java -jar yourJar.jar  {configPath}
```
