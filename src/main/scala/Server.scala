package dev.ohner

import web.{ApiService, ListingService}

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router

object Server extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val httpApp = Router(
      "/" -> ListingService.htmlService,
      "/api"-> ApiService.apiService)
      .orNotFound

    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
