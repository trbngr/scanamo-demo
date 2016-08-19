package com.company.dynamodb

import com.amazonaws.auth.BasicAWSCredentials
import com.typesafe.config.{Config, ConfigFactory}

object DynamoDBConfig {
  case class MissingConfigPath(path: String) extends Throwable(s"Missing config path: $path")

}

case class DynamoDBConfig(config: Config = ConfigFactory.load().getConfig("aws")) {
  private val referenceConfig = ConfigFactory.defaultReference().getConfig("aws")
  private val mergedConfig = config.withFallback(referenceConfig)
  mergedConfig.checkValid(referenceConfig)

  import com.amazonaws.regions

  private def optionally[A](key: String)(f: => A)(implicit config: Config): Option[A] =
    config.hasPath(key) match {
      case true => Some(f)
      case _ => None
    }

  object AWS {

    import DynamoDBConfig.MissingConfigPath

    implicit val config = mergedConfig

    private val accessKey: String = optionally("access-key-id") {
      mergedConfig.getString("access-key-id")
    } getOrElse (throw MissingConfigPath("access-key-id"))

    private val secretKey: String = optionally("secret-key") {
      mergedConfig.getString("secret-key")
    } getOrElse (throw MissingConfigPath("secret-key"))

    val Credentials = new BasicAWSCredentials(accessKey, secretKey)

    val Region = regions.Regions.fromName(optionally("region") {
      config.getString("region")
    } getOrElse "us-west-2")
  }

  object Dynamo {
    implicit val config = mergedConfig.getConfig("dynamodb")

    val Local = optionally("local") {
      config.getBoolean("local")
    } getOrElse true
  }
}
