package ethereum

import org.apache.flink.streaming.api.functions.source.SourceFunction

class SSESourceFunction[T] extends SourceFunction[T] {
  def cancel(): Unit = ???
  def run(ctx: SourceFunction.SourceContext[T]): Unit = ???
}
