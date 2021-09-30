package dev.ohner
package model


import service.TechnologyService

import java.util.UUID

case class JobListing(company: String,
                      locations: Seq[LocationEntry],
                      technologies: Seq[TechnologyEntry],
                      roles: Seq[RoleEntry]
                     ) {
  val id: UUID = UUID.randomUUID()
}

object JobListing {

  private val paragraphRegex = raw"<p>"
  private val sectionRegex = raw"\||is a" // todo also split at "is a" in "$name is a bla" because some people don't get it

  def fromComment(comment: Comment): Option[JobListing] = {

    def parseLocations(headlineSections: Array[String]) = {
      headlineSections.lift(2)
        .toVector
        .flatMap(_.split(",")) // TODO do not split in cases like "Philadelphia, PA"
        .map(_.trim)
        //        .map(_.toLowerCase)
        .map(locationName => new LocationEntry(locationName))
    }

    def parseRoles(headlineSections: Array[String]) = {
      headlineSections.lift(1)
        .map(_.split("or|,"))
        .getOrElse(Array.empty)
        .map(_.trim)
        .map(new RoleEntry(_))
    }

    def parseCompanyName(headlineSections: Array[String]) = {
      headlineSections.head
    }

    val text = comment.text

    val paragraphs = text.split(paragraphRegex)
    if (paragraphs.isEmpty) return None

    val headlineSections = paragraphs.head.split(sectionRegex)
    if (headlineSections.isEmpty) return None

    val companyName = parseCompanyName(headlineSections)
    val locations = parseLocations(headlineSections)
    val roles = parseRoles(headlineSections)
    val technologies = TechnologyService.findTechnologies(text.toLowerCase)

    Some(JobListing(
      company = companyName,
      locations = locations,
      technologies = technologies,
      roles = roles
    ))

  }


}

