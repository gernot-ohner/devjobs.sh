package dev.ohner
package ui

import scalatags.Text
import scalatags.Text.all._

object Pages {
  def index(currentQueryDescription: String = "", listings: Seq[(String, String)] = Seq.empty): Text.TypedTag[String] = {
    html(
      head(
        link(
          rel := "stylesheet",
          href := "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css",
        ),
      ),
      body(
        div(cls := "container")(
          br,
          h1("devjobs.io"),
          h2("Looking for a job?"),
          br,
          form(action := "/listings", method := "post")(
            input(name := "location", `type` := "text", placeholder := "Find by location"),
            input(name := "technology", `type` := "text", placeholder := "Find by technology"),
            input(`type` := "submit"),
          ),
          br,
          if(currentQueryDescription.nonEmpty) { p(b(currentQueryDescription)) } else {},
          div(
            for ((name, fullText) <- listings)
              yield p(b(name), p(fullText)),
          ),
        ),
      ),
    )
  }
}
