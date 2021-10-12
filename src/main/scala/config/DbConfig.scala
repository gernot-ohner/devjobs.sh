package dev.ohner
package config

import cats.effect.IO
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._

case class DbConfig(driver: String, url: String, user: String, pw: String)
case class FullConfig(database: DbConfig)

object FullConfig {
  def load: IO[FullConfig] = {
    IO {
      ConfigSource.default.load[FullConfig]
    }.flatMap {
      case Left(e) => IO.raiseError[FullConfig](new ConfigReaderException[FullConfig](e))
      case Right(value) => IO.pure(value)
    }
  }
}
