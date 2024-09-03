import Dependencies._

ThisBuild / scalaVersion := "2.12.19"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val fraudDetectionExample = (project in file("fraud-detection-example"))
  .settings(
    name := "fraud-detection-example",
    assembly / mainClass := Some("fraudExample.FraudExample"),
    libraryDependencies ++= Seq(
      "org.apache.flink" %% "flink-streaming-scala" % "1.20.0",
      "org.apache.flink" %% "flink-walkthrough-common" % "1.14.6"
    )
  )

lazy val root = (project in file("."))
  .aggregate(fraudDetectionExample)
  .settings(
    name := "flink-jobs"
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
