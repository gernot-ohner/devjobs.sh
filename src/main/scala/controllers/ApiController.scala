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
        Ok(dbService.flatMap(_.listingsByLocationAndTechnology(location, technology).map(_.mkString(", "))))
      case GET -> Root / "listings" :? TechnologyParamMatcher(technology) =>
        Ok(dbService.flatMap(_.listingsByTechnology(technology).map(_.mkString(", "))))
      case GET -> Root / "technologies" =>
        Ok(dbService.flatMap(_.technologies.map(_.mkString(", "))))
      case GET -> Root / "locations" =>
        Ok(dbService.flatMap(_.locations.map(_.mkString(", "))))
    }
  }
}
