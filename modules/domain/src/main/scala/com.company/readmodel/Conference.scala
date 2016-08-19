package com.company.readmodel

import java.time.OffsetDateTime

import com.company.States.{EntityState, Unpublished}
import com.company.{ConferenceId, EntityId, IdEquality}

sealed trait ConferenceEntity {
  val conferenceId: ConferenceId
  val id: EntityId
  val state: EntityState
}

case class Conference(id: ConferenceId,
                      details: Details = Details(),
                      venue: Venue = Venue(),
                      speakers: Set[Speaker] = Set.empty,
                      sessions: Set[Session] = Set.empty
                     )

case class Details(title: Option[String] = None, description: Option[String] = None, organizerAccountId: EntityId = EntityId.empty, startDate: OffsetDateTime = OffsetDateTime.now(), endDate: OffsetDateTime = OffsetDateTime.now(), timeZone: String = "UTC")
case class Venue(name: Option[String] = None)

case class Session(
                    conferenceId: ConferenceId,
                    id: EntityId,
                    state: EntityState = Unpublished
                  ) extends ConferenceEntity with IdEquality

case class Speaker(
                    conferenceId: ConferenceId,
                    id: EntityId,
                    state: EntityState = Unpublished,
                    published: Boolean = false,
                    email: Option[String] = None,
                    first: Option[String] = None,
                    last: Option[String] = None,
                    middle: Option[String] = None,
                    salutation: Option[String] = None,
                    suffix: Option[String] = None,
                    attributes: Map[String, String] = Map.empty) extends ConferenceEntity with IdEquality