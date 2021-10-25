package dev.ohner
package service

import errors.CustomError
import model.DTechnology

import cats.data.EitherT
import cats.effect.IO

import java.util.UUID
import scala.util.matching.Regex

object TechnologyService {
  def findTechnologies(source: String) =
    technologiesRegex.map(_.findAllMatchIn(source.appended(' '))
      .map(tech => DTechnology(UUID.randomUUID(), tech.toString()))
      .distinctBy(_.name)
      .toSeq)

  private val cs = new CrawlerService()

  private val technologies: EitherT[IO, CustomError, Seq[String]] = cs.crawlTechnologies()

  private val technologiesRegex: EitherT[IO, CustomError, Regex] =
    technologies.map(_
      .map(s => s"(?<=[\\p{Space}\\p{Punct}])(${Regex.quote(s)})(?=[\\p{Space}\\p{Punct}])")
      .reduce((s1, s2) => s"$s1|$s2")
      .r)
}
