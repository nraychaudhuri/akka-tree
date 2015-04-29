package nworks.actortree.producer

import akka.actor._
import util.OrgChanger._
import scala.concurrent.duration._

object AdvisoryBoard {
  def props: Props = Props(new AdvisoryBoard())

  val name = "advisory-board"
}

class AdvisoryBoard extends Actor with ActorLogging {

  import AdvisoryBoard._

  for(nr <- Range(1, 3))(hire(Advisor.props(nr), Advisor.name(nr), (nr * 5).seconds))

  def receive = {
    case message => log.debug(s"Actor $name received message: $message")
  }
}
