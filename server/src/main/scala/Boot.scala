import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern.ask

object Boot extends App {
  val conf = ConfigFactory.load

  implicit val system = ActorSystem("congressman")


  implicit val timeout = Timeout(50 seconds)

  val service = system.actorOf(Props[ServiceHandler])

  IO(Http) ? Http.Bind(service, interface = conf.getString("cota-parlamentar.bind"),
                       port = conf.getInt("cota-parlamentar.port"))
}