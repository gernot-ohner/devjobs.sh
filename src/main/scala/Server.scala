package dev.ohner

import web.ListingService

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.blaze.server.BlazeServerBuilder

object Server extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val listingService = ListingService.listingService
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(listingService.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
