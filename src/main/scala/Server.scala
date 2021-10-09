package dev.ohner

import service.DbAccessService

import scalatags.Text
import scalatags.Text.all._

object Server extends cask.MainRoutes {

  var currentQueryDescription = ""
  var listings: Seq[(String, String)] = Seq()

  @cask.get("/")
  def index(): Text.TypedTag[String] = {
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
          form(action := "/", method := "post")(
            //          label(`for` := "locationInput")("Location: ")
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

  @cask.postForm("/")
  def postFilter(location: String, technology: String): Text.TypedTag[String] = {
    println(s"filtering for $location and $technology")
    val dba = DbAccessService.establishConnection()
    val queryResults: Seq[(String, String)] = (location.nonEmpty, technology.nonEmpty) match {
      case (true, true) => dba.getListingsByLocationAndTechnology(location, technology)
      case (true, false) => dba.getListingsByLocation(location)
      case (false, true) => dba.getListingsByTechnology(technology)
      case (false, false) => dba.getListings.map(l => (l._2, l._3))
    }
    listings = queryResults
    currentQueryDescription = s"Showing $technology jobs ${if (location.nonEmpty) s"in $location" else "everywhere"}"
    dba.close()
    index()
  }

  initialize()
}
