import Dependencies._
import sbt.Keys._

lazy val buildSettings = Seq(
  version := "1.0",
  organization := "com.company",
  organizationName := "com.company",
  scalaVersion := "2.11.8",
  scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-Xlint:-infer-any", "-Xfatal-warnings", "-language:postfixOps"),
  testOptions in Test += Tests.Argument("-oD")
)

lazy val root = project.in(file("."))
  .aggregate(domain, scanamoImpl)
  .settings(buildSettings: _*)
  .settings(
    name := "scanamo-demo",
    moduleName := "scanamo-demo",
    publishArtifact := false
  )

lazy val domain = project.in(file("modules/domain"))
  .settings(buildSettings: _*)
  .settings(
    name := "domain",
    moduleName := "domain"
  )

lazy val scanamoImpl = project.in(file("modules/scanamoImpl"))
  .dependsOn(domain)
  .settings(buildSettings: _*)
  .settings(
    name := "scanamoImpl",
    moduleName := "scanamoImpl",
    libraryDependencies ++= Seq(dynamoDB, scanamo, typesafeConfig),
    libraryDependencies ++= Seq(scalaTest)
  )
    

