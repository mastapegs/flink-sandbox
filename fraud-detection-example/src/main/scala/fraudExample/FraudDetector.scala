package fraudExample

import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.walkthrough.common.entity.Transaction
import org.apache.flink.walkthrough.common.entity.Alert
import org.apache.flink.util.Collector
import org.apache.flink.api.common.state.ValueState
import org.apache.flink.api.common.functions.OpenContext
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.api.common.typeinfo.Types
import fraudExample.FraudDetector.flagState

class FraudDetector extends KeyedProcessFunction[Long, Transaction, Alert] {
  override def open(openContext: OpenContext): Unit = {
    val flagDescriptor = new ValueStateDescriptor("flag", Types.BOOLEAN)
    FraudDetector._flagState = getRuntimeContext().getState(flagDescriptor)
  }

  @throws[Exception]
  override def processElement(
      transaction: Transaction,
      context: KeyedProcessFunction[Long, Transaction, Alert]#Context,
      collector: Collector[Alert]
  ): Unit = {
    FraudDetector.flagState.foreach { _ =>
      if (transaction.getAmount() > FraudDetector.LARGE_AMOUNT) {
        val alert = new Alert()
        alert.setId(transaction.getAccountId())
        collector.collect(alert)
      }
      FraudDetector._flagState.clear()
    }

    if (transaction.getAmount() < FraudDetector.SMALL_AMOUNT)
      FraudDetector._flagState.update(true)
  }
}

object FraudDetector {
  private val serialVersionUID = 1L;
  private val SMALL_AMOUNT = 1.00
  private val LARGE_AMOUNT = 500.00
  private val ONE_MINUTE = 60 * 1000
  @transient private var _flagState: ValueState[java.lang.Boolean] = null
  @transient private def flagState =
    Option(_flagState.value()).map(_.booleanValue())
}
