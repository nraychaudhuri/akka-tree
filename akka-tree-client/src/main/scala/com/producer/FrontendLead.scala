package nworks.actortree.producer

import akka.actor._
import util.OrgChanger._
import scala.concurrent.duration._

object FrontendLead {
  def props: Props = Props(new FrontendLead)

  val name = "frontend-lead"

  case object Quit
}

class FrontendLead extends Actor with ActorLogging {
  import FrontendLead._

  println("Frontend Lead created")
  for(nr <- Range(1, 10))(hire(FrontendDeveloper.props(nr), FrontendDeveloper.name(nr), nr.seconds))

  def receive = {
    case FrontendDeveloper.Quit(nr) =>
      fire(FrontendDeveloper.props(nr), FrontendDeveloper.name(nr))
      hire(FrontendDeveloper.props(nr), FrontendDeveloper.name(nr), 2.seconds)
  }
}
