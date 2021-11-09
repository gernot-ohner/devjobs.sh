package dev.ohner
package model


import errors.{CommentParsingFailed, CustomError}
import service.ParserService.{parseCompanyName, parseLocations}
import service.TechnologyService

import cats.data.EitherT
import cats.implicits._
import cats.effect.IO
import io.chrisdavenport.fuuid.FUUID

case class JobListing(id: FUUID,
                      company: String,
                      locations: Seq[DLocation],
                      technologies: Seq[DTechnology],
                      text: String,
                     )

object JobListing {

  private val paragraphRegex = raw"<p>"
  private val sectionRegex = raw"\||is a"

  // TODO this is madness!
  //   do I really want to return an IO here?
  //   the only async stuff I do is find out which tags to look for
  //   and that definitely doesn't need to happen in the context of this call
  //   but rather once, at the beginning of my program
  def fromComment(comment: Comment): EitherT[IO, CustomError, JobListing] = {
    // TODO not that it make the code a whole lot better,
    //   but I think there is a way to rewrite this with for comprehension
    val paragraphs = comment.text.split(paragraphRegex)
    // TODO this does not look good. Comment parsing has nothing to do with IO
    //   but now I have to wrap it, because some sibling operation does?
    if (paragraphs.isEmpty) return EitherT.left(IO {
      CommentParsingFailed("There are no paragraphs")
    })

    val headlineSections = paragraphs.head.split(sectionRegex)
    if (headlineSections.isEmpty) return EitherT.left(IO {
      CommentParsingFailed("There are no headline sections")
    })

    val fuuid = FUUID.randomFUUID[IO]
    val companyName = parseCompanyName(headlineSections)
    val locations = parseLocations(headlineSections).sequence
    val technologies: EitherT[IO, CustomError, Seq[DTechnology]] = TechnologyService.findTechnologies(comment.text.toLowerCase)

    technologies.semiflatMap { techs =>
      locations.flatMap { locs =>
        fuuid.map { id =>
          JobListing(
            id = id,
            company = companyName,
            locations = locs,
            technologies = techs,
            text = comment.text
          )
        }
      }
    }
  }
}

