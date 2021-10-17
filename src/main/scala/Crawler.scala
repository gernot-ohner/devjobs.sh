package dev.ohner

import model.{DListing, JobListing}

import service.{CrawlerService, DbService}

import cats.effect.unsafe.implicits.global

object Crawler {

  def main(args: Array[String]): Unit = {
    createTables()
    fillTables()
    explainTables()
  }

  private def createTables(): Unit = {
    val service = DbService.fromDefaultConfig
    val tableCreationResult = service.createTables
    println(tableCreationResult)
  }

  private def fillTables(): Unit = {
    val service = DbService.fromDefaultConfig
    val cs = new CrawlerService()
    val jobListings = cs.crawlComments().take(5)
      .map(JobListing.fromComment)
      .filter(_.isDefined)
      .map(_.get)
    val listings = jobListings.map(jl => DListing(jl.id, jl.company, jl.text))

    val insertListingsResult = service.insertListings(listings)
    val insertLocations = jobListings.map(jl => jl.locations).map(service.insertLocations)
    val insertTechnologies = jobListings.map(jl => jl.technologies).map(service.insertTechnologies)
    val insertLocationRelationResult = jobListings.map(jl =>
      jl.locations.map(loc =>
        service.insertLocationRelation(jl.id, loc.id)))
    val insertTechnologyRelationResult = jobListings.map(jl =>
      jl.technologies.map(tech =>
        service.insertTechnologyRelation(jl.id, tech.id)))

    println(insertListingsResult)
    println(insertLocations)
    println(insertTechnologies)
    println(insertLocationRelationResult)
    println(insertTechnologyRelationResult)
  }

  private def explainTables(): Unit = {
    val service = DbService.fromDefaultConfig
    service.listingsByTechnology("java").attempt.map(println(_)).unsafeRunSync()
    service.listingsByLocation("remote").attempt.map(println(_)).unsafeRunSync()
    service.listingsByLocationAndTechnology("remote", "java")
      .map(println).unsafeRunSync()
  }
}
