package ethereum

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import javassist.bytecode.analysis.ControlFlow.Block
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

trait SSEData[T] extends Serializable {
  def parseJson(str: String): Either[Throwable, T]
  def typeInfo: TypeInformation[T]
}

case class BlockHead(
    hash: String,
    miner: String,
    number: Long,
    parentHash: String
)

case class TxnData(hash: String, blockHash: String, to: String, from: String, value: String)

object SSEData {
  def apply[T: SSEData] = implicitly[SSEData[T]]

  implicit val blockHeadData: SSEData[BlockHead] = new SSEData[BlockHead] {
    def parseJson(str: String): Either[Throwable, BlockHead] =
      decode[BlockHead](str)
    def typeInfo: TypeInformation[BlockHead] = createTypeInformation[BlockHead]
  }

  implicit val txnData: SSEData[TxnData] = new SSEData[TxnData] {
    def parseJson(str: String): Either[Throwable, TxnData] =
      decode[TxnData](str)
    def typeInfo: TypeInformation[TxnData] = createTypeInformation[TxnData]
  }

  implicit class EthereumDataOps[D: SSEData](str: String) {
    def parseJson: Either[Throwable, D] = SSEData[D].parseJson(str)
  }
}
