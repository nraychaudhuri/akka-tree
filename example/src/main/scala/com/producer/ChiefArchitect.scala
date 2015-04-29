package nworks.actortree.producer

import akka.actor._
import scala.concurrent.duration._
import util.OrgChanger._

object ChiefArchitect {
  def props: Props = Props(new ChiefArchitect())

  val name = "chief-architect"

  case object Quit
}

class ChiefArchitect extends Actor with ActorLogging {
  import ChiefArchitect._
  import context.dispatcher

  println("Chief Architect created")

  quit(Quit, 60.seconds)

  def receive = {
    case message => log.debug(s"Actor $name received message: $message")
  }
}
