package fraudExample

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.walkthrough.common.source.TransactionSource
import org.apache.flink.walkthrough.common.sink.AlertSink
import org.apache.flink.walkthrough.common.entity.Transaction
import org.apache.flink.api.java.functions.KeySelector

object FraudExample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment()

  val transactions = env
    .addSource(new TransactionSource())
    .name("transactions")

  val alerts = transactions
    .keyBy(new KeySelector[Transaction, Long] {
      override def getKey(transaction: Transaction): Long =
        transaction.getAccountId()
    })
    .process(new FraudDetector())
    .name("fraud-detector")

  alerts
    .addSink(new AlertSink())
    .name("send-alerts")

  env.execute("Fraud Detection")
}
