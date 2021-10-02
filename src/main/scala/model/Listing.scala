package dev.ohner
package model

import slick.jdbc.PostgresProfile.api._

import java.util.UUID

case class Listing(tag: Tag) extends Table[(UUID, String, String)](tag, "LISTINGS") {
  def id = column[UUID]("LISTING_ID", O.PrimaryKey)
  def company = column[String]("LISTING_COMPANY")
  def fullText = column[String]("LISTING_FULLTEXT")
  def * = (id, company, fullText)
}

object Listing {
  def tupleFromJobListing(jobListing: JobListing): (UUID, String, String) = {
    (jobListing.id, jobListing.company, jobListing.fullText)
  }
}
