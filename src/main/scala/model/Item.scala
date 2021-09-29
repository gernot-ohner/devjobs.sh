package dev.ohner
package model

import com.github.nscala_time.time.Imports.DateTime
import io.circe.jawn

sealed abstract class Item() {
  val id: Int
  val deleted: Option[Boolean] = Option.empty
  val time: Option[DateTime] = Option.empty
  val dead: Option[Boolean] = Option.empty
  val kids: Seq[Int] = Seq.empty

  override def toString = s"Item(id=$id, deleted=$deleted, time=$time, dead=$dead, kids=$kids)"
}

object Item {

  def parseFromString(source: String): Option[Item] = {
    val json = jawn.parse(source)

    val maybeId: Option[Int] = json.toOption
      .map(json => json.findAllByKey("id"))
      .flatMap(_.headOption)
      .flatMap(_.asNumber)
      .flatMap(_.toInt)

    val maybeDeleted = json.toOption
      .map(json => json.findAllByKey("deleted"))
      .flatMap(_.headOption)
      .flatMap(_.asBoolean)

    val maybeType = json.toOption
      .map(json => json.findAllByKey("type"))
      .flatMap(_.headOption)
      .flatMap(_.asString)

    val maybeTime = json.toOption
      .map(json => json.findAllByKey("time"))
      .flatMap(_.headOption)
      .flatMap(_.asString)
      .map(DateTime.parse)

    val maybeDead = json.toOption
      .map(json => json.findAllByKey("dead"))
      .flatMap(_.headOption)
      .flatMap(_.asBoolean)

    val myText = json.toOption
      .map(json => json.findAllByKey("text"))
      .flatMap(_.headOption)
      .flatMap(_.asString)
      .getOrElse("No text found")

    val myTitle = json.toOption
      .map(json => json.findAllByKey("title"))
      .flatMap(_.headOption)
      .flatMap(_.asString)
      .getOrElse("No title found")

    val myKids = json.toOption
      .map(json => json.findAllByKey("kids"))
      .flatMap(_.headOption)
      .flatMap(_.asArray)
      .getOrElse(Vector.empty)
      .flatMap(_.asNumber)
      .flatMap(_.toInt)

    // TODO this is super WET design, refactor!

    maybeType match {
      case Some("comment") => maybeId
        .map(i => new Comment(
          id = i,
          deleted = maybeDeleted,
          time = maybeTime,
          dead = maybeDead,
          kids = myKids,
          text = myText))
      case Some("story") => maybeId
        .map(i => new Story(
          id = i,
          deleted = maybeDeleted,
          time = maybeTime,
          dead = maybeDead,
          kids = myKids,
          title = myTitle))
      case _ => None
    }
  }
}

class Comment(val id: Int,
              override val deleted: Option[Boolean] = Option.empty,
              override val time: Option[DateTime] = Option.empty,
              override val dead: Option[Boolean] = Option.empty,
              override val kids: Seq[Int] = Seq.empty,
              val text: String) extends Item {

  override def toString = s"Comment(id=$id, deleted=$deleted, time=$time, dead=$dead, kids=$kids, text=$text)"
}

class Story(val id: Int,
            override val deleted: Option[Boolean] = Option.empty,
            override val time: Option[DateTime] = Option.empty,
            override val dead: Option[Boolean] = Option.empty,
            override val kids: Seq[Int] = Seq.empty,
            val title: String) extends Item {

  override def toString = s"Post(id=$id, deleted=$deleted, time=$time, dead=$dead, kids=$kids, title=$title)"
}
