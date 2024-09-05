package ethereum

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object Main extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment()

  val countSource = env
    .addSource(new CountSourceFunction)
    .name("count-source")

  val alertSink = countSource
    .addSink(new CountSinkFunction)
    .name("log-sink")

  env.execute("Ethereum Analysis")
}
