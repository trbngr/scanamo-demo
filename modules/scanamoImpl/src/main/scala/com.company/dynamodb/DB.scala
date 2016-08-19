package com.company.dynamodb

import cats.data.Xor
import com.amazonaws.services.dynamodbv2._
import com.amazonaws.services.dynamodbv2.model._
import com.gu.scanamo.error.DynamoReadError
import com.gu.scanamo.query.{UniqueKey, UniqueKeys}
import com.gu.scanamo.{DynamoFormat, ScanamoAsync}

import scala.collection.convert.decorateAsJava._
import scala.concurrent.{ExecutionContext, Future}

trait DB extends DynamoFormats {
  private val config = DynamoDBConfig()

  lazy implicit val asyncClient: AmazonDynamoDBAsync = {
    val client = new AmazonDynamoDBAsyncClient(config.AWS.Credentials)
    config.Dynamo.Local match {
      case true =>
        client.setEndpoint("http://localhost:8000")
        client
      case _ => client.withRegion(config.AWS.Region)
    }
  }

  lazy implicit val client: AmazonDynamoDB = {
    val client = new AmazonDynamoDBClient(config.AWS.Credentials)
    config.Dynamo.Local match {
      case true =>
        client.setEndpoint("http://localhost:8000")
        client
      case _ => client.withRegion(config.AWS.Region)
    }
  }

  def createTable(tableName: String)(attributes: (Symbol, ScalarAttributeType)*) = client.createTable(
    attributeDefinitions(attributes),
    tableName,
    keySchema(attributes),
    arbitraryThroughputThatIsIgnoredByDynamoDBLocal
  )

  def put[T: DynamoFormat](tableName: String)(item: T)(implicit ec: ExecutionContext) = ScanamoAsync.put(asyncClient)(tableName)(item)

  def get[T: DynamoFormat](tableName: String)(key: UniqueKey[_])(implicit ec: ExecutionContext): Future[Option[Xor[DynamoReadError, T]]] =
    ScanamoAsync.get[T](asyncClient)(tableName)(key)

  def getAll[T: DynamoFormat](tableName: String)(keys: UniqueKeys[_])(implicit ec: ExecutionContext): Future[List[Xor[DynamoReadError, T]]] =
    ScanamoAsync.getAll[T](asyncClient)(tableName)(keys)

  protected def keySchema(attributes: Seq[(Symbol, ScalarAttributeType)]) = {
    val hashKeyWithType :: rangeKeyWithType = attributes.toList
    val keySchemas = hashKeyWithType._1 -> KeyType.HASH :: rangeKeyWithType.map(t => t._1 -> KeyType.RANGE)
    keySchemas.map { case (symbol, keyType) => new KeySchemaElement(symbol.name, keyType) }.asJava
  }

  protected def attributeDefinitions(attributes: Seq[(Symbol, ScalarAttributeType)]) = {
    val map: Seq[AttributeDefinition] = attributes.map { case (symbol, attributeType) => new AttributeDefinition(symbol.name, attributeType) }
    map.asJava
  }

  protected val arbitraryThroughputThatIsIgnoredByDynamoDBLocal = new ProvisionedThroughput(1L, 1L)
}