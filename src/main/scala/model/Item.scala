package dev.ohner
package model

import cats.data.EitherT
import cats.effect.IO
import com.github.nscala_time.time.Imports.DateTime
import io.circe.{Json, jawn}
import cats._, cats.data._, cats.implicits._

import errors.ItemParsingFailed

sealed abstract class Item() {
  val id: Int
  val deleted: Option[Boolean] = Option.empty
  val time: Option[DateTime] = Option.empty
  val dead: Option[Boolean] = Option.empty
  val kids: Vector[Int] = Vector.empty
}

object Item {
  private val relevantPostTypes = List("comment", "story")

  def parseFromJson(json: Json): Either[ItemParsingFailed, Item] = {
    val idLeft = json.findAllByKey(
      "id")
      .headOption
      .flatMap(_.asNumber)
      .flatMap(_.toInt)
    val id = idLeft
      .toRight(ItemParsingFailed(s"Could not find id in JSON in text:\n${json.toString()}"))

    val postType = json.findAllByKey("type")
      .headOption
      .flatMap(_.asString)
      .filter(s => relevantPostTypes.contains(s.toLowerCase))
      .toRight(ItemParsingFailed(s"Could not find valid type in JSON\n${json.toString()}"))

    val maybeDeleted = json.findAllByKey("deleted")
      .headOption
      .flatMap(_.asBoolean)

    val maybeTime = json.findAllByKey("time")
      .headOption
      .flatMap(_.asString)
      .map(DateTime.parse)

    val maybeDead = json.findAllByKey("dead")
      .headOption
      .flatMap(_.asBoolean)

    val myText = json.findAllByKey("text")
      .headOption
      .flatMap(s => s.asString)
      .getOrElse("No text found")

    val myTitle = json.findAllByKey("title")
      .headOption
      .flatMap(_.asString)
      .getOrElse("No title found")

    val myKids = json.findAllByKey("kids")
      .headOption
      .flatMap(_.asArray)
      .getOrElse(Vector.empty)
      .flatMap(_.asNumber)
      .flatMap(_.toInt)

    for {
      xId <- id
      xPostType <- postType
    } yield {
      xPostType match {
        case "comment" => Comment(
          id = xId,
          deleted = maybeDeleted,
          time = maybeTime,
          dead = maybeDead,
          kids = myKids,
          text = myText)
        case "story" => Story(
          id = xId,
          deleted = maybeDeleted,
          time = maybeTime,
          dead = maybeDead,
          kids = myKids,
          title = myTitle)
      }
    }
  }

  // TODO it's silly that I have to mention IO here
  //   this function has nothing to to with IO
  //  def parseFromString(source: String): EitherT[IO,ItemParsingFailed, Item] = {
  //    val json = jawn.parse(source)
  //
  //    val maybeId: Option[Int] = json.toOption
  //      .map(json => json.findAllByKey("id"))
  //      .flatMap(_.headOption)
  //      .flatMap(_.asNumber)
  //      .flatMap(_.toInt)
  //
  //    val maybeDeleted = json.toOption
  //      .map(json => json.findAllByKey("deleted"))
  //      .flatMap(_.headOption)
  //      .flatMap(_.asBoolean)
  //
  //    val maybeType = json.toOption
  //      .map(json => json.findAllByKey("type"))
  //      .flatMap(_.headOption)
  //      .flatMap(_.asString)
  //
  //    val maybeTime = json.toOption
  //      .map(json => json.findAllByKey("time"))
  //      .flatMap(_.headOption)
  //      .flatMap(_.asString)
  //      .map(DateTime.parse)
  //
  //    val maybeDead = json.toOption
  //      .map(json => json.findAllByKey("dead"))
  //      .flatMap(_.headOption)
  //      .flatMap(_.asBoolean)
  //
  //    val myText = json.toOption
  //      .map(json => json.findAllByKey("text"))
  //      .flatMap(_.headOption)
  //      .flatMap(_.asString)
  //      .getOrElse("No text found")
  //
  //    val myTitle = json.toOption
  //      .map(json => json.findAllByKey("title"))
  //      .flatMap(_.headOption)
  //      .flatMap(_.asString)
  //      .getOrElse("No title found")
  //
  //    val myKids = json.toOption
  //      .map(json => json.findAllByKey("kids"))
  //      .flatMap(_.headOption)
  //      .flatMap(_.asArray)
  //      .getOrElse(Vector.empty)
  //      .flatMap(_.asNumber)
  //      .flatMap(_.toInt)
  //
  //    // TODO this is super WET design, refactor!
  //
  //    val result = maybeType match {
  //      case Some("comment") => maybeId
  //        .map(i => new Comment(
  //          id = i,
  //          deleted = maybeDeleted,
  //          time = maybeTime,
  //          dead = maybeDead,
  //          kids = myKids,
  //          text = myText))
  //      case Some("story") => maybeId
  //        .map(i => new Story(
  //          id = i,
  //          deleted = maybeDeleted,
  //          time = maybeTime,
  //          dead = maybeDead,
  //          kids = myKids,
  //          title = myTitle))
  //      case _ => None
  //    }
  //    EitherT(result)
  //  }
}

case class Comment(id: Int,
                   override val deleted: Option[Boolean] = Option.empty,
                   override val time: Option[DateTime] = Option.empty,
                   override val dead: Option[Boolean] = Option.empty,
                   override val kids: Vector[Int] = Vector.empty,
                   text: String) extends Item {
}

case class Story(id: Int,
                 override val deleted: Option[Boolean] = Option.empty,
                 override val time: Option[DateTime] = Option.empty,
                 override val dead: Option[Boolean] = Option.empty,
                 override val kids: Vector[Int] = Vector.empty,
                 title: String) extends Item {}
