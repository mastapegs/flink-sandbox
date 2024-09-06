package ethereum

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation

case class HeadData(data: String)
case class TxnData(data: String)

trait EthereumData[T] extends Serializable {
  def wrap(str: String): T
  def typeInfo: TypeInformation[T]
}

object EthereumData {
  def apply[T: EthereumData] = implicitly[EthereumData[T]]

  implicit val headEthereumData: EthereumData[HeadData] =
    new EthereumData[HeadData] {
      def wrap(str: String): HeadData = HeadData(str)
      def typeInfo: TypeInformation[HeadData] = createTypeInformation[HeadData]
    }

  implicit val txnEthereumData: EthereumData[TxnData] =
    new EthereumData[TxnData] {
      def wrap(str: String): TxnData = TxnData(str)
      def typeInfo: TypeInformation[TxnData] = createTypeInformation[TxnData]
    }

  implicit class EthereumDataOps[D: EthereumData](str: String) {
    def wrap: D = EthereumData[D].wrap(str)
  }
}
