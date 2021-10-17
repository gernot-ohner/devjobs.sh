package dev.ohner
package db

import cats.effect._
import cats.effect.unsafe.implicits.global
import doobie._
import doobie.postgres.implicits._
import org.scalatest.{funsuite, matchers}

object DRepositoryTest {
  def main(args: Array[String]): Unit = {
    new DRepositoryTest().execute()
  }
}

class DRepositoryTest extends funsuite.AnyFunSuite with matchers.must.Matchers with doobie.scalatest.IOChecker {

  // TODO I don't want to use my prod db though!
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/postgres",
    "postgres",
    "example",
  )
//  val repo = DRepository.fromDefaultConfig.unsafeRunSync()

  // TODO understand what actually gets tested here
  test("listings") { check(DRepository.listings) }
  test("listings by company") { check(DRepository.listingsByCompany("Signal")) }
  test("listings by technology") { check(DRepository.listingsByTechnology("java")) }
  test("listings by location") { check(DRepository.listingsByLocation("remote")) }
  test("listings by location technology name") { check(DRepository.listingsByCompany("java")) }
  test("locations") { check(DRepository.locations) }
  test("technologies") { check(DRepository.technologies) }

}
