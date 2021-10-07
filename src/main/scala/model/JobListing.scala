package dev.ohner
package model


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
  private val locationRegex = raw"&|;|( and )|(,(?! ?(AL|AK|AZ|AR|CA|CO|CT|DE|DC|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY) ))".r

  def deduplicate(s: String): String = {
    s.replaceAll("\\([^()]*\\)", "")
    // TODO transform NYC into New York City
  }

  def fromComment(comment: Comment): Option[JobListing] = {

    def parseLocations(headlineSections: Array[String]) = {
      headlineSections.lift(2)
        .toVector
        .flatMap(locationRegex.split(_))
        .map(_.toLowerCase)
        .map(deduplicate)
        .map(_.trim)
        .distinct
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
      roles = roles,
      fullText = text,
    ))
  }
}

