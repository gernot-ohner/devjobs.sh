package dev.ohner

import model.JobListing
import service.DbActions.{associationInsertAction, valueInsertAction}
import service.{CrawlerService, DbAccessService}

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {

  private def technologyService = {
    HttpRoutes.of[IO] {
      // TODO is there a pattern to dry this up?
      //   like ...with(DBA)
      case GET -> _ / "technologies" =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getTechnologies.mkString(", "))
        dba.close()
        result
      case GET -> _ / "locations" =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getLocations.mkString(", "))
        dba.close()
        result
      case GET -> _ / "listings" =>
        val dba = DbAccessService.establishConnection()
        val result = Ok(dba.getListings.mkString(", "))
        dba.close()
        result
    }
  }

  def run(args: List[String]): IO[ExitCode] = {
    val httpApp = technologyService.orNotFound
    val serverBuilder = BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
    serverBuilder
  }


  def fillDb(): Unit = {
    val dba = DbAccessService.establishConnection()
    dba.createTables()

    val cs = new CrawlerService()
    val jobListings = cs.crawlComments()
      .map(JobListing.fromComment)
      .filter(_.isDefined)
      .map(_.get)
    val valueInserts = valueInsertAction(jobListings)
    val joinTableInserts = associationInsertAction(jobListings)
    dba.runInsertSync(valueInserts.andThen(joinTableInserts))
    dba.close()
  }

  def testDba(): Unit = {
    val dba = DbAccessService.establishConnection()
    val locations = dba.getLocations
    val technologies = dba.getTechnologies
    val listingsInBoston = dba.getListingsByLocation("boston")
    val listingsWithJava = dba.getCompaniesByTechnology("java")
    val listings = dba.getListings

    println(s"Found ${listings.size} listings")
    println(s"Found ${locations.size} locations")
    println(s"Found ${technologies.size} technologies: $technologies")
    println(s"Found ${listingsInBoston.size} listingsInBoston: $listingsInBoston")
    println(s"Found ${listingsWithJava.size} listingsWithJava: $listingsWithJava")
    dba.close()
  }

}
