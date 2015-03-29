import akka.actor.{Actor, ActorLogging, ActorRefFactory}
import api.{Deputados, Partidos, MasterJsonProtocol}
import spray.routing.HttpService


class ServiceHandler extends Actor with HttpService with ActorLogging {

  def actorRefFactory: ActorRefFactory = context
  implicit def executionContext = actorRefFactory.dispatcher
  import MasterJsonProtocol._
  import spray.httpx.SprayJsonSupport._

  override def receive: Receive = runRoute(
    path("partidos" / Segment) {
      partido =>
        get {
          complete(Partidos(partido))
        }
    }
    ~
    path("partidos") {
      get {
        complete(Partidos.listPartidos)
      }
    }
    ~
    path("deputados") {
      get {
        complete(Partidos.listDeputados)
      }
    }
    ~
    path("deputados" / Segment) {
      deputadoId =>
        get {
          complete(Deputados(deputadoId))
        }
    }
  )


}
