package dev.ohner
package model


import service.ParserService.{parseCompanyName, parseLocations, parseRoles}
import service.TechnologyService

import java.util.UUID

case class JobListing(company: String,
                      locations: Seq[LocationEntry],
                      technologies: Seq[TechnologyEntry],
                      roles: Seq[RoleEntry],
                      fullText: String,
                     ) {
  val id: UUID = UUID.randomUUID()
}

object JobListing {

  private val paragraphRegex = raw"<p>"
  private val sectionRegex = raw"\||is a"

  def fromComment(comment: Comment): Option[JobListing] = {


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
      roles = roles,
      fullText = text,
    ))
  }
}

