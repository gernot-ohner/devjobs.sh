package dev.ohner

import service.DbAccessService
import service.Queries.listings

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object Main {

  def main(args: Array[String]): Unit = {
    Crawler.crawl()

    val db = DbAccessService.establishConnection()
    db.run(DbAccessService.fixtures())
    val eventualUnit: Future[Unit] = db.run(listings.result).map(_.foreach {
      case (id, companyName) => println(f"id: $id\tname: $companyName")
    })
    Await.result(eventualUnit, Duration.create(5, "s"))
    db.close()
  }
}
