package dev.ohner

import model.{Comment, Item, Story}
import service.JobPostProvider

import sttp.client3._
import sttp.model.Uri

object Crawler {

  def crawl(): Unit = {

    val items = getItems(JobPostProvider.ids.map(_.id))
    val posts = items.filter(isStory).map(_.asInstanceOf[Story])

    val comments = posts.map(_.kids.take(3))
      .flatMap(getItems) // later change this to map, I don't actually want to lose the information when
      .filter(isComment)
      .map(_.asInstanceOf[Comment])
    //          .map(JobListing.fromComment)

    println("comments: ")
    println("=" * 100)
    comments.foreach(println)
    println("=" * 100)
  }

  private def getItems(ids: Seq[Int]) = {
    ids
      .map(id => getWithDefaultBackend(uri"https://hacker-news.firebaseio.com/v0/item/$id.json"))
      .map(response => response.body)
      .map(_.toOption)
      .filter(_.isDefined)
      .map(_.get)
      .map(Item.parseFromString)
      .filter(_.isDefined) // so ugly! see if I can come up with a better way
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

  //noinspection SameParameterValue
  private def getWithDefaultBackend(uri: Uri) = {

    // TODO creating a new BackendObject for each requests is terrible, change that later
    val backend = HttpURLConnectionBackend()

    val request: Request[Either[String, String], Any] = basicRequest.get(uri)
    request.send(backend)
  }

  def toString[T](title: String, seq: Seq[T]): String = {
    seq.mkString(f"$title [", ", ", "]")
  }
}
