package dev.ohner

import model.JobListing
import service.DbActions.{associationInsertAction, valueInsertAction}
import service.{CrawlerService, DbAccessService}

object Crawler {

  def main(args: Array[String]): Unit = {
    // TODO this looks about as un-functional as it gets!
    createTables()
    fillTables()
    explainTables()
  }

  def createTables(): Unit = {
    val dba = DbAccessService.establishConnection()
    dba.createTables()
    dba.close()
  }

  def fillTables(): Unit = {
    val dba = DbAccessService.establishConnection()
    val cs = new CrawlerService()
    val jobListings = cs.crawlComments()
      .map(JobListing.fromComment)
      .filter(_.isDefined)
      .map(_.get)
    val valueInserts = valueInsertAction(jobListings)
    val joinTableInserts = associationInsertAction(jobListings)
    dba.runInsertSync(valueInserts.andThen(joinTableInserts))
    dba.close()
  }

  def explainTables(): Unit = {
    val dba = DbAccessService.establishConnection()
    val locations = dba.getLocations
    val technologies = dba.getTechnologies
    val listingsInBoston = dba.getListingsByLocation("boston")
    val listingsWithJava = dba.getListingsByTechnology("scala")
    val listings = dba.getListings

    println(s"Found ${listings.size} listings")
    println(s"Found ${locations.size} locations")
    println(s"Found ${technologies.size} technologies: $technologies")
    println(s"Found ${listingsInBoston.size} listingsInBoston: $listingsInBoston")
    println(s"Found ${listingsWithJava.size} listingsWithJava: $listingsWithJava")
    dba.close()
  }
}
