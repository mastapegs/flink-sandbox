package ethereum

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._
import org.apache.flink.table.api.EnvironmentSettings

object Main extends App {
  val ETHEREUM_HEAD_URL =
    "https://ethereum.demo.thatdot.com/blocks_head"
  val ETHEREUM_TRANSACTION_URL =
    "https://ethereum.demo.thatdot.com/mined_transactions"

  val env = StreamExecutionEnvironment.getExecutionEnvironment
  val tableEnv = StreamTableEnvironment.create(env);

  val head_data = env
    .addSource(new SSESourceFunction[BlockHead](ETHEREUM_HEAD_URL))(
      SSEData[BlockHead].typeInfo
    )
    .name("head-data")

  val txn_data = env
    .addSource(new SSESourceFunction[TxnData](ETHEREUM_TRANSACTION_URL))(
      SSEData[TxnData].typeInfo
    )
    .name("txn-data")

  head_data.print("Head Data")
  txn_data.print("Transaction Data")

  env.execute("Ethereum Analysis")
}
