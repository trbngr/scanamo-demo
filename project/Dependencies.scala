import sbt._

object Dependencies {
  object Version {
    lazy val scalaTest = "2.2.6"
    lazy val aws = "1.11.26"
    lazy val scanamo = "0.6.0"
    lazy val typesafeConfig = "1.3.0"
  }

  private def awsModule(module: String, version: String = Version.aws) = "com.amazonaws" % s"aws-java-sdk-$module" % version

  lazy val dynamoDB = awsModule("dynamodb")
  lazy val scanamo = "com.gu" %% "scanamo" % Version.scanamo
  lazy val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest % "test"
  lazy val typesafeConfig = "com.typesafe" % "config" % Version.typesafeConfig
}