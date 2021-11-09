package dev.ohner
package model

import errors.ItemParsingFailed

import com.github.nscala_time.time.Imports.DateTime
import io.circe.{Json, JsonObject, jawn}
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite

class ItemTest extends AnyFunSuite with EitherValues {

  test("should parse a minimal but correct comment json correctly") {
    val input = mkJson(
      """
        |{
        |  "id": 1234,
        |  "type": "comment",
        |  "text": "this is a test"
        |}
        |""".stripMargin,
    )
    val expected = Comment(id = 1234, text = "this is a test")

    val actual: Either[ItemParsingFailed, Item] = Item.parseFromJson(input)

    assert(actual.isRight, getMessageIfFailed(actual))
    assert(actual.value == expected, getMessageIfFailed(actual))
  }


  test("should parse a minimal but correct story json correctly") {
    val input = mkJson(
      """
        |{
        |  "id": 1234,
        |  "type": "story",
        |  "title": "this is a test title"
        |}
        |""".stripMargin,
    )
    val expected = Story(id = 1234, title = "this is a test title")

    val actual: Either[ItemParsingFailed, Item] = Item.parseFromJson(input)

    assert(actual.isRight, getMessageIfFailed(actual))
    assert(actual.value == expected, getMessageIfFailed(actual))
  }


  test("should parse a full comment json correctly") {
    val now = DateTime.now().toDateTimeISO

    val inputString =
      s"""
         |{
         |  "id": 1234,
         |  "type": "comment",
         |  "text": "this is a test title",
         |  "deleted": false,
         |  "dead": false,
         |  "kids": [1235, 1236, 0, -1235]
         |}
         |""".stripMargin
    //    |  "time": "$now",
    val input = mkJson(inputString)
    val expected = Comment(id = 1234, text = "this is a test title",
//      time = Option(now),
      deleted = Option(false),
      dead = Option(false), kids = Vector(1235, 1236, 0, -1235))

    val actual: Either[ItemParsingFailed, Item] = Item.parseFromJson(input)

    assert(actual.isRight, getMessageIfFailed(actual))
    assert(actual.value === expected, getMessageIfFailed(actual))
  }

  test("should return an error on incorrect post type") {
    val input = mkJson(
      """
        |{
        |  "id": 1234,
        |  "type": "WRONG",
        |  "title": "this is a test title"
        |}
        |""".stripMargin,
    )

    val actual: Either[ItemParsingFailed, Item] = Item.parseFromJson(input)

    assert(actual.isLeft, getMessageIfFailed(actual))
    assert(actual.left.value.msg == "Could not find valid type in JSON")
  }


  private def getMessageIfFailed(actual: Either[ItemParsingFailed, Item]) = {
    if (actual.isLeft) actual.left.value.msg else ""
  }

  private def mkJson(s: String) = {
    val inputEither = jawn.parse(s)
    assert(inputEither.isRight, "Test setup failure! Parsing json failed")
    inputEither.getOrElse(Json.Null)
  }
}
