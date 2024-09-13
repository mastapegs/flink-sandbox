import sbt._

object Dependencies {
  lazy val munit = "org.scalameta" %% "munit" % "0.7.29"
  lazy val pekkoHttp = "org.apache.pekko" %% "pekko-http" % "1.0.1"
  lazy val pekkoStream = "org.apache.pekko" %% "pekko-stream" % "1.0.3"
  lazy val pekkoActor = "org.apache.pekko" %% "pekko-actor" % "1.0.3"

  val flinkVersion = "1.20.0"

  lazy val flinkStreamingScala =
    "org.apache.flink" %% "flink-streaming-scala" % flinkVersion
  lazy val flinkTableApiScalaBridge =
    "org.apache.flink" %% "flink-table-api-scala-bridge" % flinkVersion // Flink Table API for Scala
  lazy val flinkWalkthrough =
    "org.apache.flink" %% "flink-walkthrough-common" % "1.14.6"

  val circeVersion = "0.14.1"
  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeParser = "io.circe" %% "circe-parser" % circeVersion
}

// "org.apache.flink" %% "flink-scala" % "1.17.1",              // Flink Scala API
// "org.apache.flink" %% "flink-table-planner-blink" % "1.17.1", // Flink Table Planner (Blink)
// "org.apache.flink" %% "flink-table-runtime" % "1.17.1"
