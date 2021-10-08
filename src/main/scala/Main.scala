package dev.ohner

import model.{JobListing, Technology}
import service.DbActions.{associationInsertAction, valueInsertAction}
import service.{CrawlerService, DbAccessService}

import slick.lifted.Query

object Main {
  def main(args: Array[String]): Unit = {

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

    val result = dba.getCompaniesByLocation("boston")

    println(result.size)
    println(result)

    dba.close()
  }
}
