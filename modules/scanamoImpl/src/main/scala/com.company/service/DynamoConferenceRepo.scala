package com.company.service

import cats.data.Xor
import com.amazonaws.services.dynamodbv2.model._
import com.company.ConferenceId
import com.company.dynamodb.DB
import com.company.readmodel.Conference
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

  def init = createTable(tableName)('id -> ScalarAttributeType.S)

  def read(id: ConferenceId)(implicit ec: ExecutionContext): Future[Option[Conference]] =
    get[Conference](tableName)('id -> id.id) map {
      case Some(Xor.Right(conference)) => Some(conference)
      case Some(Xor.Left(error: DynamoReadError)) =>
        println(describe(error))
        None
      case _ => None
    }

  def save(model: Conference)(implicit ec: ExecutionContext): Future[Unit] = put(tableName)(model) map (r => Unit)
}
