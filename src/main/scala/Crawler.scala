package dev.ohner

import errors.{CommentParsingFailed, CustomError, ItemParsingFailed, JsonParsingFailed, RequestFailed}
import model.{Comment, DListing, DLocation, DTechnology, JobListing}
import service.{CrawlerService, DatabaseService}

import cats.data.{EitherT, Nested}
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits._

object Crawler {

  def main(args: Array[String]): Unit = {
    //    createTables()
    fillTables()
    explainTables()
  }

  private def createTables(): Unit = {
    val service = DatabaseService.fromDefaultConfig
    val tableCreationResult = service.createTables
    println(tableCreationResult)
  }

  private def justDefined[T](seq: Seq[Option[T]]) = seq
    .filter(_.isDefined).map(_.get)

  private def fillTables(): Unit = {
    val service = DatabaseService.fromDefaultConfig
    val result = new CrawlerService()
      .crawlComments()
      .flatMap(seq => seq.map(JobListing.fromComment).sequence)
      .semiflatMap { listings =>
        service.insertListings(listings.map(jl => DListing(jl.id, jl.company, jl.text))) *>
          service.insertLocations(listings.flatMap(_.locations)) *>
          service.insertTechnologies(listings.flatMap(_.technologies)) *>
          listings.flatMap(jl => jl.locations.map(loc => service.insertLocationRelation(jl.id, loc.id))).sequence *>
          listings.flatMap(jl => jl.technologies.map(tech => service.insertTechnologyRelation(jl.id, tech.id))).sequence
      }.value.unsafeRunSync()
    explain(result)
  }

  private def explain(value: Either[CustomError, Seq[Int]]): Unit = value match {
    case Left(value) => value match {
      case RequestFailed(msg) => println(s"Request failed because: $msg")
      case JsonParsingFailed(msg) => println(s"Json parsing failed because: $msg")
      case ItemParsingFailed(msg) => println(s"Item parsing failed because: $msg")
      case CommentParsingFailed(msg) => println(s"Comment parsing failed because: $msg")
    }
    case Right(value) => println("Succeeded: ", value)
  }


  private def explainTables(): Unit = {
    val service = DatabaseService.fromDefaultConfig
    service.listingsByTechnology("java").attempt.map(println(_)).unsafeRunSync()
    service.listingsByLocation("remote").attempt.map(println(_)).unsafeRunSync()
    service.listingsByLocationAndTechnology("remote", "java")
      .map(println).unsafeRunSync()
  }
}
