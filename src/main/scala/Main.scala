package dev.ohner

import model.JobListing
import service.{CrawlerService, DbAccessService}
import service.DbActions.{associationInsertAction, valueInsertAction}

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
    dba.runInsertAsync(valueInserts.andThen(joinTableInserts))

    dba.close()
  }
}
