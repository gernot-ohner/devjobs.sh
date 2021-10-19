package dev.ohner
package service

import model.DTechnology

import java.util.UUID
import scala.util.matching.Regex

object TechnologyService {
  def findTechnologies(source: String): Seq[DTechnology] =
    technologiesRegex.findAllMatchIn(source.appended(' '))
      .map(tech => DTechnology(UUID.randomUUID(), tech.toString()))
      .distinctBy(_.name)
      .toSeq

  private val cs = new CrawlerService()

  private val technologies: Seq[String] = cs.crawlTechnologies()

  private val technologiesRegex =
    technologies
      .map(s => s"(?<=[\\p{Space}\\p{Punct}])(${Regex.quote(s)})(?=[\\p{Space}\\p{Punct}])")
      .reduce((s1, s2) => s"$s1|$s2")
      .r
}
