package ethereum

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object Main extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment()

  val sseData = env
    .addSource(new SSESourceFunction)
    .name("sse-data")

  sseData.print()

  env.execute("Ethereum Analysis")
}
