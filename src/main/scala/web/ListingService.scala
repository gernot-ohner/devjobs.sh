package dev.ohner
package web

import service.DbAccessService
import ui.Pages

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.dsl.io._
import org.http4s.scalatags.scalatagsEncoder
import org.http4s.{HttpRoutes, Request}

object ListingService {

  object LocationParamMatcher extends QueryParamDecoderMatcher[String]("location")
  object TechnologyParamMatcher extends QueryParamDecoderMatcher[String]("technology")


  def listingService: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      // TODO is there a pattern to dry this up?
      //   like ...with(DBA)
      case request@POST -> Root / "listings" =>
        val arguments = parseArguments(request)
        val listings = getListings(arguments)
        Ok(Pages.index("", listings))
      case GET -> Root / "listings" =>
        Ok(Pages.index("", Seq()))
      case GET -> Root / "api" / "listings" :? LocationParamMatcher(location) +& TechnologyParamMatcher(technology) =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getListingsByLocationAndTechnology(location, technology).mkString(", "))
        dba.close()
        result
      case GET -> Root / "api" / "listings" :? TechnologyParamMatcher(technology) =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getListingsByTechnology(technology).mkString(", "))
        dba.close()
        result
      case GET -> Root / "api" / "technologies" =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getTechnologies.mkString(", "))
        dba.close()
        result
      case GET -> Root / "api" / "locations" =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getLocations.mkString(", "))
        dba.close()
        result
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
