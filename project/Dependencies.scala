import sbt._

object Dependencies {
  lazy val munit = "org.scalameta" %% "munit" % "0.7.29"
  lazy val pekkoHttp = "org.apache.pekko" %% "pekko-http" % "1.0.1"
  lazy val pekkoStream = "org.apache.pekko" %% "pekko-stream" % "1.0.3"
  lazy val pekkoActor = "org.apache.pekko" %% "pekko-actor" % "1.0.3"
  lazy val flinkStreamingScala =
    "org.apache.flink" %% "flink-streaming-scala" % "1.20.0"
  lazy val flinkWalkthrough =
    "org.apache.flink" %% "flink-walkthrough-common" % "1.14.6"
  lazy val circeCore = "io.circe" %% "circe-core" % "0.14.1"
  lazy val circeGeneric = "io.circe" %% "circe-generic" % "0.14.1"
  lazy val circeParser = "io.circe" %% "circe-parser" % "0.14.1"
}
