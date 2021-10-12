package dev.ohner
package service

import db.DRepository

import cats.effect.IO
import doobie.postgres.implicits._

object DbService {

  // TODO I think this is a bad pattern?
  //   now all calls to the DbService share the same repo and thus the same DbConnection
  private val repo = DRepository.fromDefaultConfig

  def listingsByLocationAndTechnology(location: String, tech: String): IO[List[(String, String)]] = {
    repo.flatMap(_.listingByLocationAndTechnology(location, tech)
      .map(_.map(dl => (dl.company, dl.text))))
  }

  def listingsByLocation(location: String): IO[List[(String, String)]] = {
    repo.flatMap(_.listingByLocation(location)
      .map(_.map(dl => (dl.company, dl.text))))
  }

  def listingsByTechnology(tech: String): IO[List[(String, String)]] = {
    repo.flatMap(_.listingByTechnology(tech)
      .map(_.map(dl => (dl.company, dl.text))))
  }

  def listings: IO[List[(String, String)]] = {
    repo.flatMap(_.listings
      .map(_.map(dl => (dl.company, dl.text))))
  }

  def locations: IO[List[String]] = {
    repo.flatMap(_.locations
      .map(_.map(dl => dl.name)))
  }

  def technologies: IO[List[String]] = {
    repo.flatMap(_.technologies
      .map(_.map(dt => dt.name)))
  }
}
