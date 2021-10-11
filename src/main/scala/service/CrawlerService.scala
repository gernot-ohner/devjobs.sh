package dev.ohner
package service

import model.{Comment, Item, Story}

import io.circe.jawn
import sttp.client3.{HttpURLConnectionBackend, Identity, Request, Response, SttpBackend, UriContext, basicRequest}
import sttp.model.Uri

class CrawlerService() {
  val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

  def crawlComments(): Seq[Comment] = {
    val items = getItems(JobPostProvider.ids.map(_.id))
    val posts = items.filter(isStory).map(_.asInstanceOf[Story])

    posts.map(_.kids)
      .flatMap(getItems) // later change this to map, I don't actually want to lose the information when
      .filter(isComment)
      .map(_.asInstanceOf[Comment])
  }

  def crawlTechnologies(): Seq[String] = {
    val uri = uri"https://api.stackexchange.com/2.3/tags?order=desc&sort=popular&site=stackoverflow"
    val value: Identity[Response[Either[String, String]]] = getWithDefaultBackend(uri)
    val maybeJson = value.body.toOption.flatMap(s => jawn.parse(s).toOption)
    val tags = maybeJson.map(_.findAllByKey("name")).getOrElse(List.empty)
    tags.map(_.asString).filter(_.isDefined).map(_.get)
  }

  private def getItems(ids: Seq[Int]) = {
    ids
      .map(id => getWithDefaultBackend(uri"https://hacker-news.firebaseio.com/v0/item/$id.json"))
      .map(response => response.body)
      .map(_.toOption)
      .filter(_.isDefined)
      .map(_.get)
      .map(Item.parseFromString)
      .filter(_.isDefined)
      .map(_.get)
  }

  private def isComment: Item => Boolean = {
    case _: Comment => true
    case _ => false
  }

  private def isStory: Item => Boolean = {
    case _: Story => true
    case _ => false
  }

  private def getWithDefaultBackend(uri: Uri) = {
    val request: Request[Either[String, String], Any] = basicRequest.get(uri)
    request.send(backend)
  }

  def toString[T](title: String, seq: Seq[T]): String = {
    seq.mkString(f"$title [", ", ", "]")
  }
}
