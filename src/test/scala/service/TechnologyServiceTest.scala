package dev.ohner
package service

import org.scalatest.funsuite.AnyFunSuite

class TechnologyServiceTest extends AnyFunSuite {

  test("should find technologies") {
    val input = "This is a string containing java and c# "
    val expected = Seq("java", "c#")

    val output = TechnologyService.findTechnologies(input)
    val actual = output.map(_.name)

    assert(actual == expected)
  }


  test("should find technologies even without trailing space") {
    val input = "This is a string containing java and c#"
    val expected = Seq("java", "c#")

    val output = TechnologyService.findTechnologies(input)
    val actual = output.map(_.name)

    assert(actual == expected)
  }

  test("should be able to handle string containing no technologies") {
    val input = "This is a string containing flowers and rainbows"
    val expected = Seq.empty

    val output = TechnologyService.findTechnologies(input)
    val actual = output.map(_.name)

    assert(actual == expected)
  }


  test("should be able to handle c, c# and c++") {
    val input = "This is a string containing c and c# and c++"
    val expected = Seq("c", "c#", "c++")

    val output = TechnologyService.findTechnologies(input)
    val actual = output.map(_.name)

    assert(actual == expected)
  }


  test("should contain duplicates only once") {
    val input = "This is a string containing java and java"
    val expected = Seq("java")

    val output = TechnologyService.findTechnologies(input)
    val actual = output.map(_.name)

    assert(actual == expected)
  }

}
