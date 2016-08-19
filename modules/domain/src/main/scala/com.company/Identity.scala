package com.company

import java.util.UUID

trait AggregateId {
  def value: String
}

object ConferenceId {
  def fromString(aggregateId: String): ConferenceId = ConferenceId(aggregateId.replace("conference-", ""))
  def fromUuid(aggregateId: UUID): ConferenceId = ConferenceId(aggregateId.toString)
  def generate(): ConferenceId = ConferenceId(UUID.randomUUID().toString replaceAllLiterally("-", ""))
}

case class ConferenceId(id: String) extends AggregateId {
  override def value: String = s"conference-$id"
}

object EntityId {
  def fromString(id: String) = EntityId(id)
  def generate(): EntityId = EntityId(UUID.randomUUID().toString replaceAllLiterally("-", ""))
  def empty: EntityId = EntityId("")
}

case class EntityId(value: String)

trait IdEquality {
  val id: EntityId
  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case e: IdEquality => e.id eq id
      case _ => false
    }
  }
}