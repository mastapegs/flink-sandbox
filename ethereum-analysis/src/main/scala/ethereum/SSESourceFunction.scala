package ethereum

import org.apache.flink.streaming.api.functions.source.SourceFunction

class SSESourceFunction extends SourceFunction[Long] {
  @volatile private var isRunning = true
  private var count = 0L

  def cancel(): Unit = isRunning = false
  def run(ctx: SourceFunction.SourceContext[Long]): Unit = {
    while (isRunning) {
      ctx.collect(count)
      count += 1
    }
  }
}
