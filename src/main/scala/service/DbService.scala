package dev.ohner
package service

import db.DRepository

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.implicits._
import doobie.postgres.implicits._

object DbService {

  // TODO this is also a place where I should migrate to async
  def listingsByLocationAndTechnology(location: String, tech: String): IO[List[(String, String)]] = {
    DRepository.listingByLocationAndTechnology(location, tech)
      .to[List]
      .transact(DRepository.xa)
      .map(_.map(dl => (dl.company, dl.text)))
  }

  // TODO DRY this up
  //   I just don't know how.
  def listingsByLocation(location: String): IO[List[(String, String)]] = {
    DRepository.listingByLocation(location)
      .to[List]
      .transact(DRepository.xa)
      .map(_.map(dl => (dl.company, dl.text)))
  }

  def listingsByTechnology(tech: String): IO[List[(String, String)]] = {
    DRepository.listingByTechnology(tech)
      .to[List]
      .transact(DRepository.xa)
      .map(_.map(dl => (dl.company, dl.text)))
  }

  def listings: IO[List[(String, String)]] = {
    DRepository.listings
      .to[List]
      .transact(DRepository.xa)
      .map(_.map(dl => (dl.company, dl.text)))
  }

  def locations: IO[List[String]] = {
    DRepository.locations
      .to[List]
      .transact(DRepository.xa)
      .map(_.map(dl => dl.name))
  }

  def technologies: IO[List[String]] = {
    DRepository.technologies
      .to[List]
      .transact(DRepository.xa)
      .map(_.map(dt => dt.name))
  }
}
