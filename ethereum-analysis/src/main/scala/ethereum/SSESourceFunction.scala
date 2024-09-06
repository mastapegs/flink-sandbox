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
import EthereumData.EthereumDataOps
import org.apache.flink.api.java.typeutils.ResultTypeQueryable
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.pekko.stream.OverflowStrategy
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.apache.pekko.http.scaladsl.settings.ServerSentEventSettings
import org.apache.pekko.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling

class SSESourceFunction[T: EthereumData](url: String)
    extends SourceFunction[T]
    with ResultTypeQueryable[T] {
  @volatile private var isRunning = true

  @transient private implicit var system: ActorSystem = _
  @transient private implicit var ec: ExecutionContextExecutor = _
  @transient private implicit var mat: Materializer = _

  def cancel(): Unit = {
    isRunning = false
    system.terminate()
  }

  def run(ctx: SourceFunction.SourceContext[T]): Unit = {
    system = ActorSystem(
      s"SSESourceSystem-${url.replaceAll("[^A-Za-z0-9]", "")}"
    )
    ec = system.dispatcher
    mat = Materializer(system)

    // val customSettings = ServerSentEventSettings(system).withMaxEventSize(65536)

    val responseFuture = for {
      httpResponse <- Http() singleRequest (HttpRequest(uri = url))
      entity <- Unmarshal(httpResponse.entity)
        .to[Source[ServerSentEvent, NotUsed]]
    } yield entity

    val streamCompletion = responseFuture.flatMap { case source =>
      source
        .buffer(
          1000,
          overflowStrategy = OverflowStrategy.backpressure
        )
        .runForeach(sse => ctx.collect(sse.data.wrap))
    }
    streamCompletion.onComplete { case _ =>
      if (!isRunning) system.terminate()
    }

    // Await.result(streamCompletion, Duration.Inf)
    while (isRunning) {
      Thread.sleep(1000)
    }
  }

  def getProducedType(): TypeInformation[T] = EthereumData[T].typeInfo
}
