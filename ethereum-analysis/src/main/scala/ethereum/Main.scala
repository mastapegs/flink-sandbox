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

  // WORKS
  // val query = tableEnv.sqlQuery(
  //   "SELECT COUNT(*) FROM TxnDataTable"
  // )

  // WORKS
  // val query = tableEnv.sqlQuery(
  //   "SELECT COUNT(*) FROM TxnDataTable GROUP BY TUMBLE(proctime, INTERVAL '16' SECOND)"
  // )

  // If **unbounded** streams are involved, and there are no time constraints (i.e., windows or watermarks),
  // Flink will hold on to the record in state for an indefinite period to ensure that no future matches are missed.
  val query = tableEnv.sqlQuery(
    """
    |SELECT t.`hash`, t.`value`, b.number
    |FROM TxnDataTable AS t
    |JOIN BlockHeadTable AS b
    |ON t.blockHash = b.`hash`
  """.stripMargin
  )

  val sumQuery = tableEnv.sqlQuery(
    """
    |SELECT b.number, SUM(CAST(t.`value` AS DECIMAL(38, 0))) AS total_value
    |FROM TxnDataTable AS t
    |JOIN BlockHeadTable AS b
    |ON t.blockHash = b.`hash`
    |GROUP BY b.number
  """.stripMargin
  )

  val resultStream = tableEnv.toChangelogStream(query)
  val sumStream = tableEnv.toChangelogStream(sumQuery)

  resultStream.print("Transactions")
  sumStream.print("SUM Txn Value")

  env.execute("Ethereum Analysis")
}
