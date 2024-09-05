package ethereum

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer

object Main extends App {
  val ETHEREUM_HEAD_URL =
    "https://ethereum.demo.thatdot.com/blocks_head"
  val env = StreamExecutionEnvironment.getExecutionEnvironment()

  implicit val system: ActorSystem = ActorSystem("SSESourceSource")
  implicit val mat: Materializer = Materializer(system)

  val sseData = env
    .addSource(new SSESourceFunction(ETHEREUM_HEAD_URL))
    .name("sse-data")

  sseData.print()

  env.execute("Ethereum Analysis")
}
