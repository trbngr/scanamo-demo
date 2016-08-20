package com.company.dynamodb

import com.amazonaws.services.dynamodbv2._
import com.amazonaws.services.dynamodbv2.model._
import com.gu.scanamo.query.{UniqueKey, UniqueKeys}
import com.gu.scanamo.{DynamoFormat, ScanamoAsync}

import scala.collection.convert.decorateAsJava._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.{higherKinds, implicitConversions}

trait DB extends DynamoFormats {
  val StringType = ScalarAttributeType.S
  val NumberType = ScalarAttributeType.N
  val BinaryType = ScalarAttributeType.B

  private val config = DynamoDBConfig()

  lazy implicit val client: AmazonDynamoDBAsync = {
    val client = new AmazonDynamoDBAsyncClient(config.AWS.Credentials)
    config.Dynamo.Local match {
      case true =>
        client.setEndpoint("http://localhost:8000")
        client
      case _ => client.withRegion(config.AWS.Region)
    }
  }

  def createTable(tableName: String)(attributes: (Symbol, ScalarAttributeType)*) =
    client.createTable(
      attributeDefinitions(attributes),
      tableName,
      keySchema(attributes),
      arbitraryThroughputThatIsIgnoredByDynamoDBLocal
    )

  class Table(name: String){

    def put[T: DynamoFormat](item: T)(implicit ec: ExecutionContext) =
      ScanamoAsync.put(client)(name)(item)

    def get[T: DynamoFormat](key: UniqueKey[_])(implicit ec: ExecutionContext) =
      ScanamoAsync.get[T](client)(name)(key)

    def getAll[T: DynamoFormat](keys: UniqueKeys[_])(implicit ec: ExecutionContext) =
      ScanamoAsync.getAll[T](client)(name)(keys)

    def delete[T: DynamoFormat](key: UniqueKey[_])(implicit ec: ExecutionContext) =
      ScanamoAsync.delete[T](client)(name)(key)
  }

  def useTable[V](tableName: String)(f: (Table) => Future[V]) = f(new Table(tableName))

  protected def keySchema(attributes: Seq[(Symbol, ScalarAttributeType)]) = {
    val hashKeyWithType :: rangeKeyWithType = attributes.toList
    val keySchemas = hashKeyWithType._1 -> KeyType.HASH :: rangeKeyWithType.map(t => t._1 -> KeyType.RANGE)
    keySchemas.map { case (symbol, keyType) => new KeySchemaElement(symbol.name, keyType) }.asJava
  }

  protected def attributeDefinitions(attributes: Seq[(Symbol, ScalarAttributeType)]) =
    attributes.map { case (symbol, attributeType) => new AttributeDefinition(symbol.name, attributeType) }.asJava

  protected val arbitraryThroughputThatIsIgnoredByDynamoDBLocal = new ProvisionedThroughput(1L, 1L)
}