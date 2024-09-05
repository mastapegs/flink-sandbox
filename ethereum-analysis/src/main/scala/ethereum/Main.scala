package ethereum

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object Main extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment()

  env.execute("Ethereum Analysis")
}
