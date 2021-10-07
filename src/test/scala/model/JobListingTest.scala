package dev.ohner
package model

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.PrivateMethodTester

class JobListingTest extends AnyFunSuite with PrivateMethodTester {

  test("A location without a parenthetical be unchanged") {
    val result = JobListing.deduplicate("remote")

    assert(result == "remote")
  }

  test("A location with a trailing parenthetical be truncated") {
    val result = JobListing.deduplicate("remote (us)")

    assert(result == "remote ")
  }

  test("A location with a surrounded parenthetical have the parenthetical removed") {
    val result = JobListing.deduplicate("remote (us) ok")

    assert(result == "remote  ok")
  }



}
