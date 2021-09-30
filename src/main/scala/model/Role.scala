package dev.ohner
package model

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import java.util.UUID

class Role(tag: Tag) extends Table[(Int, String)](tag, "ROLES" ) {
  def id = column[Int]("ROLE_ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("ROLE_NAME")
  def * : ProvenShape[(Int, String)]= (id, name)
  def name_index = index("name_idx", name, unique=true)
}

object Role {
  def tuplesFromJobListing(jobListing: JobListing): Seq[(UUID, String)] = {
    jobListing.technologies.map(_.*)
  }
}

class RoleEntry(val name: String) {
  val id: UUID = UUID.randomUUID()
  def * : (UUID, String) = (id, name)
}
