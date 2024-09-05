package ethereum

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object Main extends App {
  val ETHEREUM_HEAD_URL =
    "https://ethereum.demo.thatdot.com/blocks_head"
  val env = StreamExecutionEnvironment.getExecutionEnvironment()

  val sseData = env
    .addSource(new SSESourceFunction(ETHEREUM_HEAD_URL))
    .name("sse-data")

  sseData.print()

  env.execute("Ethereum Analysis")
}
