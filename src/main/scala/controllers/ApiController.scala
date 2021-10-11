package dev.ohner
package controllers

import service.DbAccessService

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object ApiController {

  object LocationParamMatcher extends QueryParamDecoderMatcher[String]("location")

  object TechnologyParamMatcher extends QueryParamDecoderMatcher[String]("technology")

  def apiService: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root / "listings" :? LocationParamMatcher(location) +& TechnologyParamMatcher(technology) =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getListingsByLocationAndTechnology(location, technology).mkString(", "))
        dba.close()
        result
      case GET -> Root / "listings" :? TechnologyParamMatcher(technology) =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getListingsByTechnology(technology).mkString(", "))
        dba.close()
        result
      case GET -> Root / "technologies" =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getTechnologies.mkString(", "))
        dba.close()
        result
      case GET -> Root / "locations" =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getLocations.mkString(", "))
        dba.close()
        result
    }
  }
}
