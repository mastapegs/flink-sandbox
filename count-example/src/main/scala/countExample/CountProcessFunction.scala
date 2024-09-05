package countExample

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector

class CountProcessFunction extends ProcessFunction[Long, Long] {
  def processElement(
      value: Long,
      ctx: ProcessFunction[Long, Long]#Context,
      out: Collector[Long]
  ): Unit =
    out.collect(value * value)
}
