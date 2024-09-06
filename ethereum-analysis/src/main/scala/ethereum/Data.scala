package ethereum

case class HeadData(data: String)
case class TxnData(data: String)

trait EthereumData[T] {
  def wrap(str: String): T
}

object EthereumData {
  def apply[T: EthereumData] = implicitly[EthereumData[T]]

  implicit val headEthereumData: EthereumData[HeadData] =
    new EthereumData[HeadData] {
      def wrap(str: String): HeadData = HeadData(str)
    }

  implicit val txnEthereumData: EthereumData[TxnData] =
    new EthereumData[TxnData] {
      def wrap(str: String): TxnData = TxnData(str)
    }

  implicit class EthereumDataOps[D: EthereumData](str: String) {
    def wrap: D = EthereumData[D].wrap(str)
  }
}
