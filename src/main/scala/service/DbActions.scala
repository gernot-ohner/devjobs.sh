package dev.ohner
package service

import slick.jdbc.PostgresProfile.api._
import model.{JobListing, Location, Technology}
import service.Queries.{listings, listingsToLocations, listingsToTechnologies, locations, technologies}

object DbActions {

  def valueInsertAction(jobListings: Seq[JobListing]): DBIOAction[Unit, NoStream, Effect.Write] = {
    val locationTuples = jobListings.flatMap(Location.tuplesFromJobListing)
      .distinctBy(_._2.toLowerCase)
    val technologyTuples = jobListings
      .flatMap(Technology.tuplesFromJobListing)
      .distinctBy(_._2.toLowerCase)
    val listingTuples = jobListings.map(jl => (jl.id, jl.company))

    val valueInserts = DBIO.seq(
      locations ++= locationTuples,
      technologies ++= technologyTuples,
      listings ++= listingTuples
    )
    valueInserts
  }

  def associationInsertAction(jobListings: Seq[JobListing]): DBIOAction[Unit, NoStream, Effect.Write] = {
    val listingToLocationTuples = jobListings
      .flatMap(jl => jl.locations
        .map(_.id)
        .map((jl.id, _)))
    val listingToTechnologyTuples = jobListings
      .flatMap(jl => jl.technologies
        .map(_.id)
        .map((jl.id, _)))
    val joinTableInserts = DBIO.seq(
      listingsToLocations ++= listingToLocationTuples,
      listingsToTechnologies ++= listingToTechnologyTuples
    )
    joinTableInserts
  }
}
