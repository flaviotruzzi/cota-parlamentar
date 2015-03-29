package api

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.aggregations.metrics.sum.Sum
import spray.json.DefaultJsonProtocol
import scala.collection.JavaConverters._
import scala.concurrent.Future
import spray.caching.{LruCache, Cache}
import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure, Try}

object MasterJsonProtocol extends DefaultJsonProtocol {
  implicit val deputadoFormat = jsonFormat2(Deputado)
  implicit val partidoFormat = jsonFormat4(Partido)
}

case class Deputado(name: String, despesa: Double)
case class Partido(name: String, deputados: Seq[Deputado], despesa: Double, gastoMedio: Double)

trait Utilities {
  val client = ElasticClient.remote("localhost", 9300)

  val cachePartido: Cache[Partido] = LruCache()
  val cacheDeputado: Cache[Deputado] = LruCache()

}

object Deputados extends Utilities {
  def apply(deputadoId: String): Future[Deputado] = cacheDeputado(deputadoId) {
    client.execute {
      search in "deputados" -> "despesa" query {
        matchPhrase("ideCadastro", deputadoId)
      } aggregations(
        aggregation sum "despesa" field "vlrLiquido",
        aggregation terms "nome" field "txNomeParlamentar",
        aggregation terms "tipo" field "txtDescricao" size 0 aggregations(aggregation sum "despesa" field "vlrLiquido")
//        aggregation terms "cnpj" field "txtCNPJCPF" size 0
      ) fetchSource false size 0
    }.map {
      result =>
        val name = result.getAggregations.get("nome").asInstanceOf[Terms].getBuckets.asScala.head.getKey
        val despesa = result.getAggregations.get("despesa").asInstanceOf[Sum].getValue
        result.getAggregations.get("tipo").asInstanceOf[Terms].getBuckets.asScala.map {
          bucket => bucket.getKey -> bucket.getAggregations.get("despesa").asInstanceOf[Sum].getValue
        } foreach println
//        result.getAggregations.get("cnpj").asInstanceOf[Terms].getBuckets.asScala.map(_.getKey) foreach println
        Deputado(name, despesa)
    }
  }
}

object Partidos extends Utilities {

  val listPartidos: Future[Seq[String]] =
    client.execute {
      search in "deputados" -> "despesa" query {
        matchall
      } aggregations(
        aggregation terms "partidos" field "sgPartido" size 0
        ) fetchSource false size 0
    }.map {
      result => {
        result.getAggregations.get("partidos").asInstanceOf[Terms].getBuckets.asScala.map(_.getKey)
      }
    }

  val listDeputados: Future[Map[String, String]] =
    client.execute {
      search in "deputados" -> "despesa" query {
        matchall
      } aggregations(
        aggregation terms "deputados" field "txNomeParlamentar" aggregations(
          aggregation terms "id" field "ideCadastro" size 0
          ) size 0
        ) fetchSource false size 0
    }.map {
      result => {
        result.getAggregations.get("deputados")
          .asInstanceOf[Terms]
          .getBuckets.asScala.map(
            bucket => {
              Try {
                bucket.getKey -> bucket.getAggregations.get("id").asInstanceOf[Terms].getBuckets.asScala.head.getKey
              }
            })
      }.collect {
        case Success((name, id)) => id -> name
      }.toMap
    }

  def apply(partido: String): Future[Partido] = cachePartido(partido) {
    client.execute {
      search in "deputados" -> "despesa" query {
        matchPhrase("sgPartido", partido)
      } aggregations (
        aggregation terms "deputados" field "txNomeParlamentar" aggregations (
          aggregation sum "total" field "vlrLiquido"
          ) size 0
        ) fetchSource false size 0
    }.map {
      result =>
        val deputados = result
          .getAggregations.get("deputados")
          .asInstanceOf[Terms]
          .getBuckets.asScala.toSeq
          .map {
          bucket =>
            Deputado(bucket.getKey, bucket.getAggregations.get("total").asInstanceOf[Sum].getValue)
        }
        val despesa = deputados.map(_.despesa).sum
        Partido(partido, deputados, despesa, despesa / deputados.length)
    }
  }
}



//http://www.camara.gov.br/internet/deputado/bandep/178864.jpg

//
//object Deputados extends ElasticConnection{
//
//  def apply()
//
//}