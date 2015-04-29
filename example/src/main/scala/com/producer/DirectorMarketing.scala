package nworks.actortree.producer

import akka.actor._
import util.OrgChanger._
import scala.concurrent.duration._

object DirectorMarketing {
  def props: Props = Props(new DirectorMarketing())

  val name = "director-marketing"
}

class DirectorMarketing extends Actor with ActorLogging {

  for(nr <- Range(1, 3))(hire(MarketingEmployee.props(nr), MarketingEmployee.name(nr), 1.second))

  def receive = {
    case MarketingEmployee.Quit(nr) =>
      fire(MarketingEmployee.props(nr), MarketingEmployee.name(nr))
      hire(MarketingEmployee.props(nr), MarketingEmployee.name(nr), 3.seconds)
  }
}
