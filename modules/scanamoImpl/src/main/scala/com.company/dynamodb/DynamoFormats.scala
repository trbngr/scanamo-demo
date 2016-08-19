package com.company.dynamodb

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import cats.data.{NonEmptyList, Xor}
import com.company.States._
import com.company.{ConferenceId, EntityId}
import com.gu.scanamo.DynamoFormat
import com.gu.scanamo.error.{InvalidPropertiesError, MissingProperty, PropertyReadError}

trait DynamoFormats {

  import DynamoFormat._

  implicit val conferenceIdFormat = coercedXmap[ConferenceId, String, IllegalArgumentException](ConferenceId.fromString)(_.id)
  implicit val entityIdFormat = coercedXmap[EntityId, String, IllegalArgumentException](EntityId.fromString)(_.value)
  implicit val offsetDateTimeFormat = coercedXmap[OffsetDateTime, String, IllegalArgumentException](OffsetDateTime.parse)(_.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
  implicit def setFormat[T](implicit f: DynamoFormat[T]): DynamoFormat[Set[T]] = xmap[Set[T], List[T]](l => Xor.right(l.toSet))(_.toList)

  implicit val entityStateFormat = {
    def toXor(name: String) = name match {
      case "Approved" => Xor.right(Approved)
      case "Attended" => Xor.right(Attended)
      case "Cancelled" => Xor.right(Cancelled)
      case "Closed" => Xor.right(Closed)
      case "Confirmed" => Xor.right(Confirmed)
      case "Declined" => Xor.right(Declined)
      case "Deleted" => Xor.right(Deleted)
      case "Duplicate" => Xor.right(Duplicate)
      case "Exchanged" => Xor.right(Exchanged)
      case "Expired" => Xor.right(Expired)
      case "Imported" => Xor.right(Imported)
      case "Pending" => Xor.right(Pending)
      case "Published" => Xor.right(Published)
      case "Proposed" => Xor.right(Proposed)
      case "Rejected" => Xor.right(Rejected)
      case "Submitted" => Xor.right(Submitted)
      case "Unpublished" => Xor.right(Unpublished)
      case "WaitListed" => Xor.right(WaitListed)
      case _ => Xor.left(InvalidPropertiesError(NonEmptyList(PropertyReadError("state", MissingProperty))))

    }
    xmap[EntityState, String](toXor)(_.toString)

  }
}
