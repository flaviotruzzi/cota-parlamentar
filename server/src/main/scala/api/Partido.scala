package api

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.aggregations.metrics.sum.Sum
import spray.json.DefaultJsonProtocol
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MasterJsonProtocol extends DefaultJsonProtocol {
//  implicit val partidoFormat = jsonFormat3(Partido)
//  implicit val deputadoFormat = jsonFormat2(Deputado)
  implicit val despesaFormat = jsonFormat1(Despesa)
}

case class Partido(name: String, deputados: List[Deputado], despesa: Despesa)
case class Deputado(name: String, despesa: Despesa)
case class Despesa(value: Double)

object PartidoOps {

  val client = ElasticClient.remote("localhost", 9300)

  def totalCost(partido: String): Future[Despesa] = client.execute {
    search in "deputados"->"despesa" query {
      matchPhrase("sgPartido", partido)
    } aggregations (
      aggregation terms "deputados" field "txNomeParlamentar" aggregations(aggregation sum "total" field "vlrLiquido"),
      aggregation sum "total" field "vlrLiquido"
    ) fetchSource false size 0
  }.map {
    result =>
      Despesa(result.getAggregations.get("total").asInstanceOf[Sum].getValue)
  }

}
