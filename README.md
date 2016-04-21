
### Step1 Set your config
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
//使用sqlStr必須指定table
//使用collection不指定table， 則預設與collection名稱相同
register=[{collection="users",table="users"},
          {collection="outruns",table="outruns"},
          {sqlStr="""
                  SELECT Gender,Profession,Education,Birthday, users._id as userId,ResidenceProvince,ResidenceCity,
                         StartTime
                  FROM users JOIN outruns ON users._id=outruns.UserId
          """,table="hello"},
          {sqlStr="""select
              ts2d(StartTime) as startDate,
              bday2age(Birthday) as ageRange,
              ResidenceProvince,
              ResidenceCity,
              Gender,
              Profession,
              COUNT(1) as count from hello
              GROUP BY ts2d(StartTime),bday2age(Birthday),ResidenceProvince,ResidenceCity,Gender,Profession
          """,table="res1"}]
showTables=["hello","res1"]
persistence=[
            {tables=["hello"],collection="mark_hello"},
            {tables=["res1"],collection="mark_res1"}
          ]

```
### Step2 package jar
```
sbt assembly
```
### Step3 Run
```
java -jar yourJar.jar  {configPath}
```
