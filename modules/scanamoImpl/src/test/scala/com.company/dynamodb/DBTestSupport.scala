package com.company.dynamodb

import com.amazonaws.services.dynamodbv2.model._

trait DBTestSupport extends DB {

  def withTable[T](tableName: String)(attributeDefinitions: (Symbol, ScalarAttributeType)*)(thunk: => T): T = {
    createTable(tableName)(attributeDefinitions: _*)
    val res = try {
      thunk
    } finally {
      client.deleteTable(tableName)
      ()
    }
    res
  }

  def usingTable[T](tableName: String)(attributeDefinitions: (Symbol, ScalarAttributeType)*)(thunk: => T): Unit = {
    withTable(tableName)(attributeDefinitions: _*)(thunk)
    ()
  }

  def withTableWithSecondaryIndex[T](tableName: String, secondaryIndexName: String)
                                    (primaryIndexAttributes: (Symbol, ScalarAttributeType)*)(secondaryIndexAttributes: (Symbol, ScalarAttributeType)*)(
                                      thunk: => T
                                    ): T = {
    client.createTable(
      new CreateTableRequest().withTableName(tableName)
        .withAttributeDefinitions(attributeDefinitions(
          primaryIndexAttributes.toList ++ (secondaryIndexAttributes.toList diff primaryIndexAttributes.toList)))
        .withKeySchema(keySchema(primaryIndexAttributes))
        .withProvisionedThroughput(arbitraryThroughputThatIsIgnoredByDynamoDBLocal)
        .withGlobalSecondaryIndexes(new GlobalSecondaryIndex()
          .withIndexName(secondaryIndexName)
          .withKeySchema(keySchema(secondaryIndexAttributes))
          .withProvisionedThroughput(arbitraryThroughputThatIsIgnoredByDynamoDBLocal)
          .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
        )
    )
    val res = try {
      thunk
    } finally {
      client.deleteTable(tableName)
      ()
    }
    res
  }
}