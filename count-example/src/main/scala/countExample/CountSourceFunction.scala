package countExample

import org.apache.flink.streaming.api.functions.source.SourceFunction

class CountSourceFunction extends SourceFunction[Long] {
  @volatile private var isRunning = true
  private var count = 0L

  def cancel(): Unit = isRunning = false
  def run(ctx: SourceFunction.SourceContext[Long]): Unit = {
    while (isRunning && count < 1000) {
      ctx.collect(count)
      count += 1
    }
  }
}
