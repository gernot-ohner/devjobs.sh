package dev.ohner
package service

import slick.jdbc.PostgresProfile.api._
import model.{JobListing, Location, Technology}
import service.Queries.{listings, listingsToLocations, listingsToTechnologies, locations, technologies}

object DbActions {

  def valueInsertAction(jobListings: Seq[JobListing]): DBIOAction[Unit, NoStream, Effect.Write] = {
    val locationTuples = jobListings.flatMap(Location.fromJobListing).distinct
    val technologyTuples = jobListings.flatMap(Technology.fromJobListing).distinct
    val listingTuples = jobListings.map(jl => (jl.id, jl.company, jl.fullText))

    DBIO.seq(
      locations ++= locationTuples,
      technologies ++= technologyTuples,
      listings ++= listingTuples,
    )
  }

  def associationInsertAction(jobListings: Seq[JobListing]): DBIOAction[Unit, NoStream, Effect.Write] = {
    val listingToLocationTuples = jobListings
      .flatMap(jl => jl.locations
        .map(location => (jl.id, location.name)))
    val listingToTechnologyTuples = jobListings
      .flatMap(jl => jl.technologies
        .map(tech => (jl.id, tech.name)))

    DBIO.seq(
      listingsToLocations ++= listingToLocationTuples,
      listingsToTechnologies ++= listingToTechnologyTuples,
    )
  }
}
