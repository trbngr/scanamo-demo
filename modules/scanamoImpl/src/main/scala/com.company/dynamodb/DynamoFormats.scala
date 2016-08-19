package com.company.dynamodb

import com.company.{ConferenceId, EntityId}
import com.gu.scanamo.{DerivedDynamoFormat, DynamoFormat}

trait DynamoFormats extends DerivedDynamoFormat {

//  implicit val conferenceFormat = ???
  implicit val conferenceIdFormat = DynamoFormat.coercedXmap[ConferenceId, String, IllegalArgumentException](ConferenceId.fromString)(_.id)
  implicit val entityIdFormat = DynamoFormat.coercedXmap[EntityId, String, IllegalArgumentException](EntityId.fromString)(_.value)
}
