package dev.ohner
package controllers

import service.DbAccessService
import ui.Pages

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.dsl.io._
import org.http4s.scalatags.scalatagsEncoder
import org.http4s.{HttpRoutes, Request}

object ListingController {

  def htmlService: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case request@POST -> Root / "listings" =>
        val arguments = parseArguments(request)
        val listings = getListings(arguments)
        Ok(Pages.index("", listings))
      case GET -> Root / "listings" =>
        Ok(Pages.index("", Seq()))
    }
  }

  private def parseArguments(request: Request[IO]) = {
    // TODO this is a terrible way to do this!
    // TODO the functional way is to just hand over the stream and not evaluate it here
    val byteVector = request.body.compile.toVector.unsafeRunSync()
    val body = byteVector.map((c: Byte) => c.toChar).mkString
    val arguments = body.split("&")
      .map(_.split("="))
      .map(arr => (arr.headOption, arr.lastOption))
      .filter(kv => kv._1.isDefined && kv._2.isDefined)
      .map(kv => (kv._1.get, kv._2.get))
      .toMap
    arguments
  }

  private def getListings(arguments: Map[String, String]) = {
    val dba = DbAccessService.establishConnection()
    val listings = if (arguments.contains("location") && arguments.contains("technology")) {
      dba.getListingsByLocationAndTechnology(arguments("location"), arguments("technology"))
    } else if (arguments.contains("location")) {
      dba.getListingsByLocation(arguments("location"))
    } else if (arguments.contains("technology")) {
      dba.getListingsByTechnology("technology")
    } else {
      dba.getListings.map(l => (l._2, l._3))
    }
    dba.close()
    listings
  }
}