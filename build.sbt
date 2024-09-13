import Dependencies._
import sbtassembly.MergeStrategy

ThisBuild / scalaVersion := "2.12.19"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val commonSettings = Seq(
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", xs @ _*) =>
      (xs.map(_.toLowerCase) match {
        case "manifest.mf" :: Nil | "index.list" :: Nil |
            "dependencies" :: Nil | "license" :: Nil | "license.txt" :: Nil |
            "license.md" :: Nil | "notice" :: Nil | "notice.txt" :: Nil |
            "notice.md" :: Nil | "io.netty.versions.properties" :: Nil |
            "versions" :: Nil =>
          MergeStrategy.discard
        case "module-info.class" :: Nil => MergeStrategy.discard
        case _                          => MergeStrategy.first
      }): MergeStrategy
    case PathList("reference.conf") =>
      MergeStrategy.concat // Merge all reference.conf files
    case PathList("application.conf") =>
      MergeStrategy.concat // In case there are multiple application.conf
    case _ => MergeStrategy.first
  },
  libraryDependencies ++= Seq(
    flinkStreamingScala,
    flinkTableApiScalaBridge,
    flinkWalkthrough
  )
)

lazy val fraudDetectionExample = (project in file("fraud-detection-example"))
  .settings(
    name := "fraud-detection-example",
    assembly / mainClass := Some("fraudExample.FraudExample"),
    commonSettings
  )

lazy val countExample = (project in file("count-example"))
  .settings(
    name := "count-example",
    assembly / mainClass := Some("countExample.Main"),
    commonSettings
  )

lazy val ethereumAnalysis = (project in file("ethereum-analysis"))
  .settings(
    name := "ethereum-analysis",
    assembly / mainClass := Some("ethereum.Main"),
    commonSettings,
    libraryDependencies ++= Seq(
      pekkoHttp,
      pekkoStream,
      pekkoActor,
      circeCore,
      circeGeneric,
      circeParser
    )
  )

lazy val root = (project in file("."))
  .aggregate(fraudDetectionExample, countExample, ethereumAnalysis)
  .settings(
    name := "flink-jobs"
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
