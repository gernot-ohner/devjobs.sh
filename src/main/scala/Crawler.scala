package dev.ohner

import model.{Comment, Item, Post}

import sttp.client3._
import sttp.model.Uri

object Crawler {

  def main(args: Array[String]): Unit = {

    val items = getWhosHiringPosts

    val comments = items.filter(isComment).map(_.asInstanceOf[Comment])
    val posts = items.filter(isPost).map(_.asInstanceOf[Post])

    println("posts: ")
    println("=" * 100)
    posts.foreach(println)
    println("=" * 100)
  }

  private def getWhosHiringPosts = {
    JobPostProvider.ids
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

  private def isPost: Item => Boolean = {
    case _: Post => true
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
