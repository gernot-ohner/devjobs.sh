package dev.ohner
package controllers

import service.DbService

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object ApiController {

  object LocationParamMatcher extends QueryParamDecoderMatcher[String]("location")

  object TechnologyParamMatcher extends QueryParamDecoderMatcher[String]("technology")

  def apiService: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root / "listings" :? LocationParamMatcher(location) +& TechnologyParamMatcher(technology) =>
        Ok(DbService.listingsByLocationAndTechnology(location, technology).map(_.mkString(", ")))
      case GET -> Root / "listings" :? TechnologyParamMatcher(technology) =>
        Ok(DbService.listingsByTechnology(technology).map(_.mkString(", ")))
      case GET -> Root / "technologies" =>
        Ok(DbService.technologies.map(_.mkString(", ")))
      case GET -> Root / "locations" =>
        Ok(DbService.locations.map(_.mkString(", ")))
    }
  }
}
