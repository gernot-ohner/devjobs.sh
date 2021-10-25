package dev.ohner

import errors.CustomError
import model.{Comment, DListing, JobListing}
import service.{CrawlerService, DatabaseService}

import cats.data.EitherT
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
    val cs = new CrawlerService()
    val comments: EitherT[IO, CustomError, Seq[Comment]] = cs.crawlComments()


//    val yolo: IO[Either[MyError, Seq[Comment]]] = comments.seq

    val jobListings = comments.flatMap(seq => seq.map(JobListing.fromComment).sequence)
//    val jobListings: EitherT[IO, MyError, Seq[JobListing]] =
//      comments.map(seq => seq.map(c => JobListing.fromComment(c))

//    jobListings.sequence
//      .map(io => io.map(justDefined))

    val listings = jobListings.map(_.map(jl => DListing(jl.id, jl.company, jl.text)))

    val insertListingsResult = listings.semiflatMap(service.insertListings)
    val insertLocations = jobListings
      .map(jls => jls.flatMap(_.locations))
      .semiflatMap(service.insertLocations)
    val insertTechnologies = jobListings
      .map(jls => jls.flatMap(_.technologies))
      .semiflatMap(service.insertTechnologies)

    val all = insertListingsResult.value &> insertLocations.value &> insertTechnologies.value
    all.unsafeRunSync()
//    val insertLocationRelationResult = jobListings
//      .map((jls: Seq[JobListing]) => jls
//        .flatMap((jl: JobListing) => jl.locations
//          .map((loc: DLocation) => service.insertLocationRelation(jl.id, loc.id))))
//    val insertTechnologyRelationResult = jobListings.map(jls =>
//      jls.flatMap(jl => jl.technologies.map(tech => service.insertTechnologyRelation(jl.id, tech.id))))



//    println(insertListingsResult)
//    println(insertLocations)
//    println(insertTechnologies)
//    println(insertLocationRelationResult)
//    println(insertTechnologyRelationResult)
  }

  private def explainTables(): Unit = {
    val service = DatabaseService.fromDefaultConfig
    service.listingsByTechnology("java").attempt.map(println(_)).unsafeRunSync()
    service.listingsByLocation("remote").attempt.map(println(_)).unsafeRunSync()
    service.listingsByLocationAndTechnology("remote", "java")
      .map(println).unsafeRunSync()
  }
}
