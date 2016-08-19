package com.company.service

import cats.data.Xor
import com.amazonaws.services.dynamodbv2.model._
import com.company.ConferenceId
import com.company.dynamodb.DB
import com.company.readmodel.{Conference, Details}
import com.gu.scanamo.ScanamoAsync
import com.gu.scanamo.error.DynamoReadError

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

object DynamoConferenceRepo {
  def apply(table: String): DynamoConferenceRepo = new DynamoConferenceRepo(table)
  def apply: DynamoConferenceRepo = apply("conferences")
}

class DynamoConferenceRepo(tableName: String) extends ConferenceRepo with DB {

  import DynamoReadError._
  import com.gu.scanamo.syntax._

  case class ConferenceDocument(id: String, details: Details)
  implicit def Conf2Doc(conf: Conference): ConferenceDocument = ConferenceDocument(conf.id.id, conf.details)
  implicit def Doc2Conf(doc: ConferenceDocument): Conference = Conference(ConferenceId.fromString(doc.id), doc.details)

  def init = createTable(tableName)('id -> ScalarAttributeType.S)

  def read(id: ConferenceId)(implicit ec: ExecutionContext): Future[Option[Conference]] = {
    val future = ScanamoAsync.get[Conference](asyncClient)(tableName)('id -> id.id)
    future map {
      case Some(Xor.Right(conference)) => Some(conference)
      case Some(Xor.Left(error: DynamoReadError)) =>
        println(describe(error))
        None
      case _ => None
    }
  }

  def save(model: Conference)(implicit ec: ExecutionContext): Future[Unit] = {
    ScanamoAsync.put(asyncClient)(tableName)(model) map (r => Unit)
  }
}
