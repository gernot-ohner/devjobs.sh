package dev.ohner
package controllers

import service.DbService

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object ApiController {

  object LocationParamMatcher extends QueryParamDecoderMatcher[String]("location")

  object TechnologyParamMatcher extends QueryParamDecoderMatcher[String]("technology")

  val dbService = DbService.fromDefaultConfig

  def apiService: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root / "listings" :? LocationParamMatcher(location) +& TechnologyParamMatcher(technology) =>
        Ok(dbService.listingsByLocationAndTechnology(location, technology).map(_.mkString(", ")))
      case GET -> Root / "listings" :? TechnologyParamMatcher(technology) =>
        Ok(dbService.listingsByTechnology(technology).map(_.mkString(", ")))
      case GET -> Root / "technologies" =>
        Ok(dbService.technologies.map(_.mkString(", ")))
      case GET -> Root / "locations" =>
        Ok(dbService.locations.map(_.mkString(", ")))
    }
  }
}
