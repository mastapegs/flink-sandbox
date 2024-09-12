package ethereum

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object Main extends App {
  val ETHEREUM_HEAD_URL =
    "https://ethereum.demo.thatdot.com/blocks_head"
  val ETHEREUM_TRANSACTION_URL =
    "https://ethereum.demo.thatdot.com/mined_transactions"

  val env = StreamExecutionEnvironment.getExecutionEnvironment()

  val head_data = env
    .addSource(new SSESourceFunction[BlockHead](ETHEREUM_HEAD_URL))
    .name("head-data")

  val txn_data = env
    .addSource(new SSESourceFunction[TxnData](ETHEREUM_TRANSACTION_URL))
    .name("txn-data")

  head_data.print("Head Data")
  txn_data.print("Transaction Data")

  env.execute("Ethereum Analysis")
}
