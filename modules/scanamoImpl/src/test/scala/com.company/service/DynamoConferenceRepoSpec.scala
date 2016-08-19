package com.company.service

import java.time.OffsetDateTime

import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import com.company.States.Unpublished
import com.company._
import com.company.dynamodb.DBTestSupport
import com.company.readmodel._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

class DynamoConferenceRepoSpec extends FlatSpec with Matchers with ScalaFutures with DBTestSupport {

  implicit val defaultPatience =
    PatienceConfig(timeout = Span(2, Seconds), interval = Span(15, Millis))
  val testTableName: String = s"TestConferences"

  "save" must "succeed" in {
    usingTable(testTableName)('id -> ScalarAttributeType.S) {
      val repo = DynamoConferenceRepo(testTableName)
      val id = ConferenceId.generate()
      val conf = Conference(
        id,
        details = Details(Some("My Cnference"), Some("Description"), EntityId.generate(), OffsetDateTime.MIN, OffsetDateTime.MAX, "UTC"),
        venue = Venue(Some("My Venue")),
        speakers = (1 to 5).map(i => Speaker(id, EntityId.generate(), Unpublished, published = false, Some("chris@example.com"), Some("Chris"), Some("Martin"), Some("Stephen"), Some("Mr."))).toSet,
        sessions = (1 to 5).map { _ => Session(id, EntityId.generate(), Unpublished) }.toSet
      )
      repo.save(conf).futureValue
//      println(repo.read(id).futureValue)
      repo.read(id).futureValue.get shouldEqual conf
    }

  }
}
