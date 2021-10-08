package dev.ohner
package model

import service.ParserService

import org.scalatest.PrivateMethodTester
import org.scalatest.funsuite.AnyFunSuite

class ParserServiceTest extends AnyFunSuite with PrivateMethodTester {

  test("A location without a parenthetical be unchanged") {
    val result = ParserService.deduplicate("remote")

    assert(result == "remote")
  }

  test("A location with a trailing parenthetical be truncated") {
    val result = ParserService.deduplicate("remote (us)")

    assert(result == "remote ")
  }

  test("A location with a surrounded parenthetical have the parenthetical removed") {
    val result = ParserService.deduplicate("remote (us) ok")

    assert(result == "remote  ok")
  }



}
