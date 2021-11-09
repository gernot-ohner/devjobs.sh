package dev.ohner
package service

import errors.CustomError
import model.DTechnology

import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import io.chrisdavenport.fuuid.FUUID
import sttp.client3.UriContext

import scala.util.matching.Regex

object TechnologyService {
  def findTechnologies(source: String) = {
    technologiesRegex
      .semiflatMap(regex => regex.findAllMatchIn(source.appended(' ')).toSeq
        .distinctBy(_.toString())
        .map(tech => {
          val result: IO[DTechnology] = FUUID.randomFUUID[IO].map(id => DTechnology(id, tech.toString()))
          result
        }).sequence,
      )
  }

  private val cs = new CrawlerService()

  private val techUri = uri"https://api.stackexchange.com/2.3/tags?order=desc&sort=popular&site=stackoverflow"
  private val technologies: EitherT[IO, CustomError, Seq[String]] = cs.crawlTechnologies(techUri)

  private val technologiesRegex: EitherT[IO, CustomError, Regex] =
    technologies.map(_
      .map(s => s"(?<=[\\p{Space}\\p{Punct}])(${Regex.quote(s)})(?=[\\p{Space}\\p{Punct}])")
      .reduce((s1, s2) => s"$s1|$s2")
      .r)
}
