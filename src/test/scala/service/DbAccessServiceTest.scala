//import dev.ohner.service.Queries.{listings, listingsToLocations, locations}
//import slick.dbio.{DBIO, DBIOAction}

//import slick.jdbc.PostgresProfile.api._

//import java.util.UUID
//package dev.ohner
//package service
//
//import dev.ohner.service.Queries.listings
//import slick.dbio.DBIO
//
//class DbAccessServiceTest extends org.scalatest.flatspec.AnyFlatSpec {
//
//  "test 1" should "return joined db entries" in {
//    val db = DbAccessService.establishConnection()
//    // right, this is a kind of a silly thing to test, since it ruins my db
//    DbAccessService.fixtures()
//
//    db.run(listings.)
//
//    db.close()
//  }
//
//}


//def fixtures(): DBIOAction[Unit, NoStream, Effect.Schema with Effect.Write] = {
//
//  val nycId = UUID.randomUUID()
//  val bostonId = UUID.randomUUID()
//  val yCombinatorId = UUID.randomUUID()
//  val amazonId = UUID.randomUUID()
//  DBIO.seq(
//    locations.schema.create,
//    locations += (nycId, "New York City"),
//    locations += (bostonId, "Boston"),
//
//    listings.schema.create,
//    listings += (yCombinatorId, "Y Combinator"),
//    listings += (amazonId, "Amazon"),
//
//    listingsToLocations.schema.create,
//    listingsToLocations += (yCombinatorId, bostonId),
//    listingsToLocations += (amazonId, bostonId),
//    listingsToLocations += (amazonId, nycId),
//  )
//}
