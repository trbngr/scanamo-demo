package com.company.service

import java.time.OffsetDateTime

import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import com.company.States.Unpublished
import com.company._
import com.company.dynamodb.DBTestSupport
import com.company.readmodel._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

class DynamoConferenceRepoSpec extends FlatSpec with Matchers with ScalaFutures with DBTestSupport {

  val testTableName: String = s"TestConferences"

  "save" must "succeed" in {
    usingTable(testTableName)('id -> ScalarAttributeType.S) {
      val repo = DynamoConferenceRepo(testTableName)
      val id = ConferenceId.generate()
      val conf = Conference(
        id,
        details = Details("My Cnference", "Description", EntityId.generate(), OffsetDateTime.MIN, OffsetDateTime.MAX, "UTC"),
        venue = Venue("My Venue"),
        speakers = (1 to 100).map(i => Speaker(id, EntityId.generate(), Unpublished, published = false, "chris@example.com", "Chris", "Martin", "Stephen", "Mr.")).toSet,
        sessions = (1 to 100).map { _ => Session(id, EntityId.generate(), Unpublished) }.toSet
      )
      repo.save(conf).futureValue
      repo.read(id).futureValue shouldEqual Some(conf)
    }


  }
}
