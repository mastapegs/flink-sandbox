package fraudExample

import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.walkthrough.common.entity.Transaction
import org.apache.flink.walkthrough.common.entity.Alert
import org.apache.flink.util.Collector
import org.apache.flink.api.common.state.ValueState
import org.apache.flink.api.common.functions.OpenContext
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.api.common.typeinfo.Types
import java.lang

class FraudDetector extends KeyedProcessFunction[Long, Transaction, Alert] {
  @transient private var _flagState: ValueState[java.lang.Boolean] = null
  @transient private def flagState =
    Option(_flagState.value()).map(_.booleanValue())

  @transient private var _timerState: ValueState[lang.Long] = null
  @transient private def timerState = Option(_timerState.value()).map(_.toLong)

  override def open(openContext: OpenContext): Unit = {
    val flagDescriptor = new ValueStateDescriptor("flag", Types.BOOLEAN)
    _flagState = getRuntimeContext().getState(flagDescriptor)

    val timerDescriptor = new ValueStateDescriptor("timer-state", Types.LONG)
    _timerState = getRuntimeContext().getState(timerDescriptor)
  }

  override def onTimer(
      timestamp: Long,
      ctx: KeyedProcessFunction[Long, Transaction, Alert]#OnTimerContext,
      out: Collector[Alert]
  ): Unit = {
    _flagState.clear()
    _timerState.clear()
  }

  @throws[Exception]
  override def processElement(
      transaction: Transaction,
      context: KeyedProcessFunction[Long, Transaction, Alert]#Context,
      collector: Collector[Alert]
  ): Unit = {
    flagState.foreach { _ =>
      if (transaction.getAmount() > FraudDetector.LARGE_AMOUNT) {
        val alert = new Alert()
        alert.setId(transaction.getAccountId())
        collector.collect(alert)
      }
      _flagState.clear()
    }

    if (transaction.getAmount() < FraudDetector.SMALL_AMOUNT)
      _flagState.update(true)

    val timer =
      context.timerService().currentProcessingTime() + FraudDetector.ONE_MINUTE
    context.timerService().registerProcessingTimeTimer(timer)
    _timerState.update(timer)
  }
}

object FraudDetector {
  private val serialVersionUID = 1L;
  private val SMALL_AMOUNT = 1.00
  private val LARGE_AMOUNT = 500.00
  private val ONE_MINUTE = 60 * 1000
}
