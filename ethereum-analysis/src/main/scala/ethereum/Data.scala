package ethereum

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation

case class HeadData(data: String)
case class TxnData(data: String)

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
