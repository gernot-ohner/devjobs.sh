package dev.ohner
package service

import model.TechnologyEntry

import scala.util.matching.Regex

object TechnologyService {

  // TODO or should this be regexes?
  def technologies(): Seq[String] = {
    // TODO get this list from somewhere else instead of hardcoding it
    val cs = new CrawlerService()
    cs.crawlTechnologies()
//    Seq(
//      "java",
//      "scala",
//      "c++",
//      "rust",
//      "c#"
//    )
  }
  // make an extra list for things like Go that shouldn't be case insensitive
  // TODO and I still need to handle things like "Go" vs "golang"

  def regexTechnologies(): Seq[Regex] = {
    Seq("(?i)Java".r)
  }

  // TODO I might want to make technologies their own type at some point
  def findTechnologies(source: String): Seq[TechnologyEntry] = {
    // TODO make this more efficient!
    //   is there actually a way to make this more efficient than O(n * m)
    //   where n is number of words in source and m is number of technologies?

    //   the most efficient way would be a single regex that matches every
    //   technology and captures what it matched

    technologies()
      .filter(tech => source.contains(tech.toLowerCase))
      .map(new TechnologyEntry(_))
  }

}
