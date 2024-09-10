package ethereum

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation

case class HeadData(data: String)
case class TxnData(data: String)

case class BlockHead(
    baseFeePerGas: String,
    blobGasUsed: String,
    difficulty: String,
    excessBlobGas: String,
    extraData: String,
    gasLimit: Long,
    gasUsed: Long,
    hash: String,
    logsBloom: String,
    miner: String,
    mixHash: String,
    nonce: String,
    number: Long,
    parentBeaconBlockRoot: String,
    parentHash: String,
    receiptsRoot: String,
    sha3Uncles: String,
    stateRoot: String,
    timestamp: Long,
    totalDifficulty: String,
    transactionsRoot: String,
    withdrawalsRoot: String
)

trait SSEData[T] extends Serializable {
  def wrap(str: String): T
  def typeInfo: TypeInformation[T]
}

object SSEData {
  def apply[T: SSEData] = implicitly[SSEData[T]]

  implicit val headEthereumData: SSEData[HeadData] =
    new SSEData[HeadData] {
      def wrap(str: String): HeadData = HeadData(str)
      def typeInfo: TypeInformation[HeadData] = createTypeInformation[HeadData]
    }

  implicit val txnEthereumData: SSEData[TxnData] =
    new SSEData[TxnData] {
      def wrap(str: String): TxnData = TxnData(str)
      def typeInfo: TypeInformation[TxnData] = createTypeInformation[TxnData]
    }

  implicit class EthereumDataOps[D: SSEData](str: String) {
    def wrap: D = SSEData[D].wrap(str)
  }
}
