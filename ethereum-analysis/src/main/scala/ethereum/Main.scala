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

  val blockHeadTable = tableEnv.fromDataStream(
    head_data,
    $"hash",
    $"miner",
    $"number",
    $"parentHash"
  )
  val txnDataTable = tableEnv.fromDataStream(
    txn_data,
    $"hash",
    $"blockHash",
    $"to",
    $"from",
    $"value",
    $"proctime".proctime
  )

  tableEnv.createTemporaryView("BlockHeadTable", blockHeadTable)
  tableEnv.createTemporaryView("TxnDataTable", txnDataTable)

  head_data.print("Head Data")
  txn_data.print("Transaction Data")

  // WORKS
  // val query = tableEnv.sqlQuery(
  //   "SELECT COUNT(*) FROM TxnDataTable GROUP BY TUMBLE(proctime, INTERVAL '16' SECOND)"
  // )

  // WORKS
  // val query = tableEnv.sqlQuery(
  //   "SELECT COUNT(*) FROM TxnDataTable"
  // )

  // If **unbounded** streams are involved, and there are no time constraints (i.e., windows or watermarks),
  // Flink will hold on to the record in state for an indefinite period to ensure that no future matches are missed.
  val query = tableEnv.sqlQuery(
    """
    |SELECT t.`hash`, b.`hash`
    |FROM TxnDataTable AS t
    |JOIN BlockHeadTable AS b
    |ON t.blockHash = b.`hash`
  """.stripMargin
  )

  val resultStream = tableEnv.toChangelogStream(query)
  resultStream.print("Count")

  env.execute("Ethereum Analysis")
}
