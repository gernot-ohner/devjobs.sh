package dev.ohner
package service

import config.FullConfig
import db.DRepository

import cats.effect.IO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux
import doobie.{Query0, Transactor}

class DbService(val xa: Aux[IO, Unit]) {

  def transact[A](q: Query0[A]) = q.to[List].transact(xa)

  def listingsByLocationAndTechnology(location: String, tech: String): IO[List[(String, String)]] = {
    transact(DRepository.listingsByLocationAndTechnology(location, tech))
      .map(_.map(dl => (dl.company, dl.text)))
  }

  def listingsByLocation(location: String): IO[List[(String, String)]] = {
    transact(DRepository.listingsByLocation(location))
      .map(_.map(dl => (dl.company, dl.text)))
  }

  def listingsByTechnology(tech: String): IO[List[(String, String)]] = {
    transact(DRepository.listingsByTechnology(tech))
      .map(_.map(dl => (dl.company, dl.text)))
  }

  def listings: IO[List[(String, String)]] = {
    transact(DRepository.listings)
      .map(_.map(dl => (dl.company, dl.text)))
  }

  def locations: IO[List[String]] = {
    transact(DRepository.locations)
      .map(_.map(dl => dl.name))
  }

  def technologies: IO[List[String]] = {
    transact(DRepository.technologies)
      .map(_.map(dt => dt.name))
  }
}

object DbService {
  def fromDefaultConfig = {
    val dbConfig = FullConfig.load.map(c => c.database)
    dbConfig.map(dbc => new DbService(
      Transactor.fromDriverManager[IO](dbc.driver, dbc.url, dbc.user, dbc.pw)))
  }
}
