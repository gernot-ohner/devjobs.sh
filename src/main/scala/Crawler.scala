package dev.ohner

import model.JobListing
import service.DbActions.{associationInsertAction, valueInsertAction}
import service.{CrawlerService, DbAccessService}

object Crawler {

  var currentQueryDescription = ""
  var listings: Seq[(String, String)] = Seq()

  def main(args: Array[String]): Unit = {
    fillDb()
    testDba()
  }


  def fillDb(): Unit = {
    val dba = DbAccessService.establishConnection()
    dba.createTables()

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

  def testDba(): Unit = {
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
