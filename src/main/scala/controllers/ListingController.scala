package dev.ohner
package controllers

import service.DatabaseService
import ui.Pages

import cats.effect.IO
import fs2.Chunk
import org.http4s.dsl.io._
import org.http4s.scalatags.scalatagsEncoder
import org.http4s.{HttpRoutes, Request}

object ListingController {

  val dbService = DatabaseService.fromDefaultConfig

  def htmlService: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case request@POST -> Root / "listings" =>
        parseArguments(request)
          .flatMap(arguments => getListings(arguments))
          .flatMap(listings => Ok(Pages.index("", listings)))
      case GET -> Root / "listings" =>
        Ok(Pages.index("", Seq()))
    }
  }

  private def parseArguments(request: Request[IO]) = {

    request.body
      .map(c => c.toChar)
      .split(_ == '&')
      .map((c: Chunk[Char]) => c.toString())
      .map(s => s.split("="))
      .map(arr => (arr.headOption, arr.lift(1)))
      .filter(kv => kv._1.isDefined && kv._2.isDefined)
      .map(kv => (kv._1.get, kv._2.get))
      .compile.toList.map(_.toMap)
  }

  private def getListings(arguments: Map[String, String]) = {
    if (arguments.contains("location") && arguments.contains("technology")) {
      dbService.listingsByLocationAndTechnology(arguments("location"), arguments("technology"))
    } else if (arguments.contains("location")) {
      dbService.listingsByLocation(arguments("location"))
    } else if (arguments.contains("technology")) {
      dbService.listingsByTechnology(arguments("technology"))
    } else {
      dbService.listings
    }
  }
}
