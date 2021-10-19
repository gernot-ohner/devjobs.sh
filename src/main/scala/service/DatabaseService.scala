package dev.ohner
package service

import config.FullConfig
import db.DRepository
import model.{DListing, DLocation, DTechnology}

import cats._
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux
import doobie.{Query0, Transactor}

import java.util.UUID


class DatabaseService(val xa: Aux[IO, Unit]) {

  def transact[A](q: Query0[A]) = q.to[List].transact(xa)

  def insertLocations(locs: Seq[DLocation]) = locs
    .map(DRepository.insertLocation)
    .map(_.update.run.transact(xa))
    .sequence
    .unsafeRunSync()

  def insertLocationRelation(listingId: UUID, locationId: UUID) = {
    DRepository.insertLocationRelation(listingId, locationId)
      .update.run
      .transact(xa).unsafeRunSync()
  }

  def insertTechnologyRelation(listingId: UUID, techId: UUID) = {
    DRepository.insertTechnologyRelation(listingId, techId)
      .update.run
      .transact(xa).unsafeRunSync()
  }

  def insertTechnologies(techs: Seq[DTechnology]) = techs
    .map(DRepository.insertTechnology)
    .map(_.update.run.transact(xa))
    .sequence
    .unsafeRunSync()

  def insertListings(listings: Seq[DListing]) = listings
    .map(DRepository.insertListing)
    .map(_.update.run.transact(xa))
    .sequence
    .unsafeRunSync()

  // TODO this should happen in flyway
  def createTables = {
    (DRepository.createTableTechnologies
      ++ DRepository.createTableLocations
      ++ DRepository.createTableListings
      ).update.run.transact(xa).unsafeRunSync()

    DRepository.createTableListingsToLocations.update.run
      .transact(xa)
      .unsafeRunSync()

    DRepository.createTableListingsToTechnologies.update.run
      .transact(xa)
      .unsafeRunSync()
  }

  def listingsByLocationAndTechnology(location: String, tech: String): IO[List[(String, String)]] = {
    transact(DRepository.listingsByLocationAndTechnology(location, tech))
      .map(_.map(dl => (dl.company, dl.text)))
  }

  def listingsByLocation(location: String): IO[List[(String, String)]] = {
    transact(DRepository.listingsByLocation(location))
      .map(_.map(dl => (dl.company, dl.text)))
  }

  def listingsByTechnology(tech: String): IO[List[(String, String)]] = {
    val query = DRepository.listingsByTechnology(tech)
    transact(query)
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
    val technologies1 = DRepository.technologies
    val transaction = technologies1.to[List].transact(xa)
    transaction.map(_.map(dt => dt.name))
  }
}

object DatabaseService {
  def fromDefaultConfig = {
    val dbc = FullConfig.load.map(c => c.database).unsafeRunSync()
    val xa = Transactor.fromDriverManager[IO](dbc.driver, dbc.url, dbc.user, dbc.pw)
    new DatabaseService(xa)
  }
}
