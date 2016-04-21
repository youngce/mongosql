package exts

import java.util

import com.typesafe.config.Config

/**
  * Created by mark on 2016/4/21.
  */
object RichConfigExts {
  implicit class RichConfig(val underlying: Config) extends AnyVal {
    private def optionalize[T](path:String)(func:Config=>T)={
      if (underlying.hasPath(path)) {
        Some(func(underlying))
      } else {
        None
      }
    }
    def getOptBoolean(path: String): Option[Boolean] = {
      optionalize(path)(_.getBoolean(path))

    }
    def getOptString(path: String): Option[String] = {
      optionalize(path)(_.getString(path))

    }
    def getOptStringList(path: String): Option[util.List[String]] = {
      optionalize(path)(_.getStringList(path))

    }
  }

}
