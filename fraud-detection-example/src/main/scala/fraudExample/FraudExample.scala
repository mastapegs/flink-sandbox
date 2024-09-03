package fraudExample

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.walkthrough.common.source.TransactionSource
import org.apache.flink.walkthrough.common.sink.AlertSink

object FraudExample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment()

  val transactions = env
    .addSource(new TransactionSource())
    .name("transactions")

  val alerts = transactions
    .keyBy(_.getAccountId())
    .process(new FraudDetector())
    .name("fraud-detector")

  alerts
    .addSink(new AlertSink())
    .name("send-alerts")

  env.execute("Fraud Detection")
}
