package dev.ohner
package service

import model.TechnologyEntry

import scala.util.matching.Regex

object TechnologyService {


  // TODO deal with technologies like C and R
  def technologies(): Seq[String] = {
    val cs = new CrawlerService()
    cs.crawlTechnologies()
  }
  // make an extra list for things like Go that shouldn't be case insensitive
  // TODO and I still need to handle things like "Go" vs "golang"

  // TODO I might want to make technologies their own type at some point
  def findTechnologies(source: String): Seq[TechnologyEntry] = {
    // TODO make this more efficient!
    //   is there actually a way to make this more efficient than O(n * m)
    //   where n is number of words in source and m is number of technologies?

    //   the most efficient way would be a single regex that matches every
    //   technology and captures what it matched

    // TODO profile how much time is spent here
    technologies()
      .filter(tech => source.contains(tech.toLowerCase))
      .map(new TechnologyEntry(_))
  }

}
