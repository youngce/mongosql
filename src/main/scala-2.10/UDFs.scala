/**
  * Created by mark on 2016/4/15.
  */
import org.apache.spark.sql.functions.udf
import org.joda.time.{LocalDate, DateTime}
object UDFs{
  def bday2age=(b_day: java.sql.Timestamp) => {
    //年齡,default:0無設定.1:15岁以下，2:15-24，3:25-34，4:35-44，5:45-54，6:55-64，7:65岁以上

    val age=DateTime.now().getYear-b_day.getYear
    age match {
      case t if t < 15 => 1
      case t if 15<=t&&t< 25 => 2
      case t if 25<=t&&t< 35 => 3
      case t if 35<=t&&t< 45 => 4
      case t if 45<=t&&t< 55 => 5
      case t if 55<=t&&t< 65 => 6
      case _ => 7
    }}

  def dt2d=(dt: Long) => {
    //Calendar.getInstance().setTimeInMillis(dt.getTime)
    new java.sql.Date(dt)

  }
}
