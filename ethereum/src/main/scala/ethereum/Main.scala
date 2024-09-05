package fraudExample

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import ethereum.SSESourceFunction

object Main extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment()

  val sseSource = env
    .addSource(new SSESourceFunction())
    .name("server-sent-event-source")

  env.execute("Ethereum Analysis")
}
