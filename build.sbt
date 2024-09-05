import Dependencies._
import sbtassembly.MergeStrategy

ThisBuild / scalaVersion := "2.12.19"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val fraudDetectionExample = (project in file("fraud-detection-example"))
  .settings(
    name := "fraud-detection-example",
    assembly / mainClass := Some("fraudExample.FraudExample"),
    assemblyMergeStrategy in assembly := {
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
      case _ => MergeStrategy.first
    },
    libraryDependencies ++= Seq(
      "org.apache.flink" %% "flink-streaming-scala" % "1.20.0",
      "org.apache.flink" %% "flink-walkthrough-common" % "1.14.6"
    )
  )

lazy val ethereum = (project in file("ethereum"))
  .settings(
    name := "ethereum",
    assembly / mainClass := Some("ethereum.Main"),
    assemblyMergeStrategy in assembly := {
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
      case _ => MergeStrategy.first
    },
    libraryDependencies ++= Seq(
      "org.apache.flink" %% "flink-streaming-scala" % "1.20.0",
      "org.apache.flink" %% "flink-walkthrough-common" % "1.14.6"
    )
  )

lazy val root = (project in file("."))
  .aggregate(fraudDetectionExample, ethereum)
  .settings(
    name := "flink-jobs"
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
