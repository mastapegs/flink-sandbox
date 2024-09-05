package ethereum

import org.apache.flink.streaming.api.functions.source.SourceFunction

class SSESourceFunction(url: String) extends SourceFunction[String] {
  @volatile private var isRunning = true
  private var count = 0L

  def cancel(): Unit = isRunning = false
  def run(ctx: SourceFunction.SourceContext[String]): Unit = {
    while (isRunning) {
      ctx.collect(s"URL: ${url}, Count: ${count.toString()}")
      count += 1
    }
  }
}
