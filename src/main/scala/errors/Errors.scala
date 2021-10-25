package dev.ohner
package errors

sealed trait CustomError extends Throwable

case class RequestFailed(msg: String) extends CustomError
case class JsonParsingFailed(msg: String) extends CustomError
case class ItemParsingFailed(msg: String) extends CustomError
case class CommentParsingFailed(msg: String) extends CustomError

