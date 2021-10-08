package dev.ohner
package service

import model.{LocationEntry, RoleEntry}

object ParserService {

  private val locationRegex = raw"&|;|( or )|( and )|(,(?! ?(AL|AK|AZ|AR|CA|CO|CT|DE|DC|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY) ))".r

  // TODO what I should really do is do a keyword search in the whole text
  //   rather than trying to parse the first line,
  //   because many listings are inconsistent
  def normalizeLocation(location: String): String = {
    location.replaceAll("fully remote", "remote")
      .replaceAll("remote[- ]ok", "remote")
      .replaceAll("(?i)NYC", "New York City")
  }

  def parseLocations(headlineSections: Array[String]): Seq[LocationEntry] = {
    headlineSections.lift(2)
      .toVector
      .flatMap(locationRegex.split(_))
      .map(_.toLowerCase)
      .map(deduplicate)
      .map(normalizeLocation)
      .map(_.trim)
      .distinct
      .map(locationName => new LocationEntry(locationName))
  }

  def parseRoles(headlineSections: Array[String]): Seq[RoleEntry] = {
    headlineSections.lift(1)
      .map(_.split("or|,"))
      .getOrElse(Array.empty)
      .map(_.trim)
      .map(new RoleEntry(_))
  }

  def parseCompanyName(headlineSections: Array[String]): String = {
    deduplicate(headlineSections.head).trim
  }

  def deduplicate(s: String): String = {
    s.replaceAll("\\([^()]*\\)", "")
    // TODO transform NYC into New York City
  }


}
