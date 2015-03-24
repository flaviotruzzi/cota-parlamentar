import akka.actor.{Actor, ActorLogging, ActorRefFactory}
import api.{MasterJsonProtocol, PartidoOps}
import spray.routing.HttpService


class ServiceHandler extends Actor with HttpService with ActorLogging {

  def actorRefFactory: ActorRefFactory = context
  implicit def executionContext = actorRefFactory.dispatcher
  import MasterJsonProtocol._
  import spray.httpx.SprayJsonSupport._

  override def receive: Receive = runRoute(
    path("partido" / Segment) {
      (partido) =>
        get {
          complete{
            PartidoOps.totalCost(partido)
          }
        }
    }
  )


}
