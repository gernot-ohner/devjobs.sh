package dev.ohner
package service

import model.{Comment, Item, Story}
import errors._

import cats.data.{EitherT, Kleisli}
import cats.effect.{IO, Resource}
import io.circe.{Json, ParsingFailure, jawn}
import sttp.capabilities.fs2.Fs2Streams
import sttp.client3.http4s.Http4sBackend
import sttp.client3.{HttpURLConnectionBackend, Identity, Request, Response, SttpBackend, UriContext, basicRequest}
import sttp.model.Uri
import cats._
import cats.effect.unsafe.implicits.global
import cats.implicits._
import cats.instances._
import errors.RequestFailed

class CrawlerService() {
  val backend = Http4sBackend.usingDefaultBlazeClientBuilder[IO](): Resource[IO, SttpBackend[IO, Fs2Streams[IO]]]

  def crawlComments(): EitherT[IO, CustomError, Seq[Comment]] = {
    val items: EitherT[IO, CustomError, Seq[Item]] = getItems(JobPostProvider.ids.map(_.id))
    val stories = items.map(justTs[Story])
    val kids: EitherT[IO, CustomError, Seq[Item]] = stories.flatMap(seq => getItems(seq.flatMap(_.kids)))
    val comments = kids.map(justTs[Comment])
    comments
  }

  def crawlTechnologies(): EitherT[IO, CustomError, Seq[String]] = {
    val uri = uri"https://api.stackexchange.com/2.3/tags?order=desc&sort=popular&site=stackoverflow"
    getWithDefaultBackendEitherT(uri)
      .subflatMap(jawnParseWithCustomError)
      .map(_.findAllByKey("name").map(_.toString()))
  }


  private def getItems(ids: Seq[Int]) =
    ids.map(getSingleItem).sequence

  private def getSingleItem(id: Int): EitherT[IO, CustomError, Item] = {
    getWithDefaultBackendEitherT(uri"https://hacker-news.firebaseio.com/v0/item/$id.json")
      .subflatMap(s => jawnParseWithCustomError(s))
      .subflatMap(json => Item.parseFromJson(json))
  }

  def jawnParseWithCustomError(s: String) = {
    jawn.parse(s).left.map(pf => JsonParsingFailed(pf.getMessage()))
  }

  private def isComment: Item => Boolean = {
    case _: Comment => true
    case _ => false
  }

  private def isStory: Item => Boolean = {
    case _: Story => true
    case _ => false
  }

  private def getWithDefaultBackendEitherT(uri: Uri): EitherT[IO, CustomError, String] = {
    EitherT(backend.use(be => basicRequest
        .get(uri)
        .send(be))
        .map(_.body)
        .map(_.left.map(RequestFailed)))
  }

  private def justTs[T <: Item](seq: Seq[_]): Seq[T] = seq
    // warning "abstract type T is unchecked because it is eliminated by erasure"
    .filter(_.isInstanceOf[T])
    .map(_.asInstanceOf[T])
}
