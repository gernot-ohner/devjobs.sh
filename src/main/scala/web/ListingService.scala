package dev.ohner
package web

import service.DbAccessService

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object ListingService {

  object LocationParamMatcher extends QueryParamDecoderMatcher[String]("location")
  object TechnologyParamMatcher extends QueryParamDecoderMatcher[String]("technology")


  def listingService: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      // TODO is there a pattern to dry this up?
      //   like ...with(DBA)
      case GET -> _ / "listings" :? LocationParamMatcher(location) +& TechnologyParamMatcher(technology) =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getListingsByLocationAndTechnology(location, technology).mkString(", "))
        dba.close()
        result
      case GET -> _ / "listings" :? TechnologyParamMatcher(technology) =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getListingsByTechnology(technology).mkString(", "))
        dba.close()
        result
      case GET -> _ / "listings" :? TechnologyParamMatcher(technology) =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getListingsByTechnology(technology).mkString(", "))
        dba.close()
        result

      case GET -> _ / "technologies" =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getTechnologies.mkString(", "))
        dba.close()
        result
      case GET -> _ / "locations" =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getLocations.mkString(", "))
        dba.close()
        result
      case GET -> _ / "listings" =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getListings.mkString(", "))
        dba.close()
        result
    }
  }


}
