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
import SSEData.EthereumDataOps
import org.apache.flink.api.java.typeutils.ResultTypeQueryable
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.pekko.stream.OverflowStrategy
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.apache.pekko.http.scaladsl.settings.ServerSentEventSettings
import org.apache.pekko.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling
import scala.concurrent.ExecutionContext

class SSESourceFunction[T: SSEData](url: String)
    extends SourceFunction[T]
    with ResultTypeQueryable[T] {
  @volatile private var isRunning = true

  @transient private implicit var system: ActorSystem = _
  @transient private implicit var ec: ExecutionContextExecutor = _

  def cancel(): Unit = {
    isRunning = false
    system.terminate()
  }

  def run(ctx: SourceFunction.SourceContext[T]): Unit = {
    system = ActorSystem(
      s"SSESourceSystem-${url.replaceAll("[^A-Za-z0-9]", "")}"
    )
    ec = system.dispatcher

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
        .runForeach(sse => {
          sse.data.parseJson match {
            case Left(error)      => println(error)
            case Right(blockHead) => ctx.collect(blockHead)
          }
        })
    }
    streamCompletion.onComplete { case _ =>
      if (!isRunning) system.terminate()
    }

    Await.result(streamCompletion, Duration.Inf)
  }

  def getProducedType(): TypeInformation[T] = SSEData[T].typeInfo
}
