package dev.ohner
package model


import service.ParserService.{parseCompanyName, parseLocations}
import service.TechnologyService

import java.util.UUID

case class JobListing(company: String,
                      locations: Seq[DLocation],
                      technologies: Seq[DTechnology],
                      text: String,
                     ) {
  val id: UUID = UUID.randomUUID()
}

object JobListing {

  private val paragraphRegex = raw"<p>"
  private val sectionRegex = raw"\||is a"

  def fromComment(comment: Comment): Option[JobListing] = {
    // TODO not that it make the code a whole lot better,
    //   but I think there is a way to rewrite this with for comprehension
    val paragraphs = comment.text.split(paragraphRegex)
    if (paragraphs.isEmpty) return None

    val headlineSections = paragraphs.head.split(sectionRegex)
    if (headlineSections.isEmpty) return None

    val companyName = parseCompanyName(headlineSections)
    val locations = parseLocations(headlineSections)
    val technologies = TechnologyService.findTechnologies(comment.text.toLowerCase)

    Some(JobListing(
      company = companyName,
      locations = locations,
      technologies = technologies,
      text = comment.text,
    ))
  }
}

