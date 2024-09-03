package fraudExample

import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.walkthrough.common.entity.Transaction
import org.apache.flink.walkthrough.common.entity.Alert
import org.apache.flink.util.Collector

class FraudDetector extends KeyedProcessFunction[Long, Transaction, Alert] {
  @throws[Exception]
  override def processElement(
      transaction: Transaction,
      context: KeyedProcessFunction[Long, Transaction, Alert]#Context,
      collector: Collector[Alert]
  ): Unit = {
    val alert = new Alert()
    alert.setId(transaction.getAccountId)

    collector.collect(alert)
  }
}

object FraudDetector {
  private val serialVersionUID = 1L;
  private val SMALL_AMOUNT = 1.00
  private val LARGE_AMOUNT = 500.00
  private val ONE_MINUTE = 60 * 1000
}
