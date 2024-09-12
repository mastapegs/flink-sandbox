package ethereum

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import javassist.bytecode.analysis.ControlFlow.Block
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

// case class HeadData(data: String)
// case class TxnData(data: String)

case class BlockHead(
    hash: String,
    miner: String,
    number: Long,
    parentHash: String
)

trait SSEData[T] extends Serializable {
  def parseJson(str: String): Either[Throwable, T]
  def typeInfo: TypeInformation[T]
}

object SSEData {
  def apply[T: SSEData] = implicitly[SSEData[T]]

  implicit val blockHeadData: SSEData[BlockHead] = new SSEData[BlockHead] {
    def parseJson(str: String): Either[Throwable, BlockHead] =
      decode[BlockHead](str)
    def typeInfo: TypeInformation[BlockHead] = createTypeInformation[BlockHead]
  }

  // implicit val headEthereumData: SSEData[HeadData] =
  //   new SSEData[HeadData] {
  //     def wrap(str: String): HeadData = HeadData(str)
  //     def typeInfo: TypeInformation[HeadData] = createTypeInformation[HeadData]
  //   }

  // implicit val txnEthereumData: SSEData[TxnData] =
  //   new SSEData[TxnData] {
  //     def wrap(str: String): TxnData = TxnData(str)
  //     def typeInfo: TypeInformation[TxnData] = createTypeInformation[TxnData]
  //   }

  implicit class EthereumDataOps[D: SSEData](str: String) {
    def parseJson: Either[Throwable, D] = SSEData[D].parseJson(str)
  }
}
