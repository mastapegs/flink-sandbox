package countExample

import org.apache.flink.streaming.api.functions.sink.SinkFunction

class CountSinkFunction extends SinkFunction[Long] {
  override def invoke(value: Long, context: SinkFunction.Context): Unit =
    println(s"Count: ${value}")
}
