package dev.ohner
package service

import com.github.nscala_time.time.Imports.{DateTime, DateTimeFormat}

object JobPostProvider {

  private val monthFormatter = DateTimeFormat.forPattern("MM/yyyy")

  // TODO check out if this endpoint does what I want:
  //   https://hackernews.api-docs.io/v0/live-data/job-hn-stories
  def ids: Seq[JobPostUri] = {
    Seq(
      JobPostUri(28380661, DateTime.parse("09/2021", monthFormatter)),
      JobPostUri(28037366, DateTime.parse("08/2021", monthFormatter)),
      JobPostUri(27699704, DateTime.parse("07/2021", monthFormatter)),
      JobPostUri(27355392, DateTime.parse("06/2021", monthFormatter)),
      JobPostUri(27025922, DateTime.parse("05/2021", monthFormatter)),
    )
  }
}

case class JobPostUri(id: Int, description: DateTime)
