package ethereum

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import scala.concurrent.ExecutionContextExecutor
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.HttpRequest
import org.apache.pekko.http.scaladsl.unmarshalling.Unmarshal
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._
import org.apache.pekko.http.scaladsl.model.sse.ServerSentEvent
import org.apache.pekko.NotUsed
import scala.util.Success
import scala.util.Failure

class SSESourceFunction(url: String) extends SourceFunction[String] {
  @volatile private var isRunning = true
  private var count = 0L

  @transient private implicit var system: ActorSystem = _
  @transient private implicit var ec: ExecutionContextExecutor = _

  def cancel(): Unit = {
    isRunning = false
    system.terminate()
  }

  def run(ctx: SourceFunction.SourceContext[String]): Unit = {
    system = ActorSystem("SSESourceSystem")
    ec = system.dispatcher

    val responseFuture = for {
      httpResponse <- Http() singleRequest (HttpRequest(uri = url))
      entity <- Unmarshal(httpResponse.entity)
        .to[Source[ServerSentEvent, NotUsed]]
    } yield entity

    responseFuture.onComplete {
      case Success(source) => source.runForeach(sse => ctx.collect(sse.data))
      case Failure(exception) =>
        println(s"Failed to connect to SSE source: ${exception.getMessage}")
    }
    // Old Logic below

    while (isRunning) {
      Thread.sleep(1000)
    }
  }
}
