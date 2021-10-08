package dev.ohner
package service

import model.{Listing, ListingToLocation, ListingToTechnology}

import slick.dbio.Effect
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

// TODO does the fact that its a field in this class
// mean that the DB connection gets closed if the object is destroyed?
class DbAccessService(val db: JdbcBackend.DatabaseDef) {
  def getListingsByLocationAndTechnology(targetLocation: String, targetTechnology:String): Seq[(String, String)] = {
    val action = for {
      ((listing, listingToLocation), listingToTechnology) <- Queries.listings join Queries.listingsToLocations on (_.id === _.listingId) join Queries.listingsToTechnologies on ((tuple: (Listing, ListingToLocation), technology: ListingToTechnology) => (tuple: (Listing, ListingToLocation))._1.id === technology.listingId)
      if listingToLocation.locationId === targetLocation && listingToTechnology.technologyId === targetTechnology

    } yield (listing.company, listing.fullText)
    val result = Await.result(db.run(action.result), Duration.create("5s"))
    result
  }

  def getListingsByLocation(targetLocation: String): Seq[(String, String)] = {
    val action = for {
      (listing, listingToLocation) <- Queries.listings join Queries.listingsToLocations on (_.id === _.listingId)
      if listingToLocation.locationId === targetLocation
    } yield (listing.company, listing.fullText)
    val result = Await.result(db.run(action.result), Duration.create("5s"))
    result
  }

  def getListingsByTechnology(targetTechnology: String): Seq[(String, String)] = {
    val action = for {
      (listing, listingToTechnology) <- Queries.listings join Queries.listingsToTechnologies on (_.id === _.listingId)
      if listingToTechnology.technologyId === targetTechnology
    } yield (listing.company, listing.fullText)
    val result = Await.result(db.run(action.result), Duration.create("5s"))
    result
  }

  def getTechnologies: Seq[String] = {
    val action = Queries.technologies.sortBy(_.name).result
    val result = Await.result(db.run(action), Duration.create("5s"))
    result
  }

  def getLocations: Seq[String] = {
    val action = Queries.locations.sortBy(_.name).result
    val result = Await.result(db.run(action), Duration.create("5s"))
    result
  }

  def getListings: Seq[(UUID, String, String)] = {
    val action = Queries.listings.result
    val result = Await.result(db.run(action), Duration.create("5s"))
    result
  }


  def close(): Unit = db.close()

  def createTables(): Unit = {
    Queries.all.foreach(query => {
      val futureDrop = db.run(query.schema.dropIfExists)
      Await.result(futureDrop, Duration.create(5, "s"))
    })
    Queries.all.foreach(query => db.run(query.schema.create))
  }

  def runInsert(action: DBIOAction[Unit, NoStream, Effect.Write]): Future[Unit] = {
    db.run(action)
  }

  def runInsertSync(action: DBIOAction[Unit, NoStream, Effect.Write]): Unit = {
    Await.result(db.run(action), Duration.create(5, "s"))
  }
}

object DbAccessService {
  def establishConnection(): DbAccessService = {
    new DbAccessService(
      Database.forURL(
        url = "jdbc:postgresql://localhost:5432/postgres",
        user = "postgres",
        password = "example",
        driver = "org.postgresql.Driver"),
    )
  }
}
