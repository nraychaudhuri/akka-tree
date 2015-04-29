package nworks.actortree.producer

import akka.actor._
import util.OrgChanger._
import scala.concurrent.duration._

object BackendLead {
  def props: Props = Props(new BackendLead)

  val name = "backend-lead"

  case object Quit
}

class BackendLead extends Actor with ActorLogging {
  import BackendLead._

  println("Backend Lead created")
  for(nr <- Range(1, 15))(hire(BackendDeveloper.props(nr), BackendDeveloper.name(nr), (nr * 500).milliseconds))

  def receive = {
    case BackendDeveloper.Quit(nr) =>
      fire(BackendDeveloper.props(nr), BackendDeveloper.name(nr))
      hire(BackendDeveloper.props(nr), BackendDeveloper.name(nr), 3.seconds)
  }
}
