package nworks.actortree.producer

import akka.actor._
import util.OrgChanger._
import scala.concurrent.duration._

object DirectorSales {
  def props: Props = Props(new DirectorSales())

  val name = "director-sales"
}

class DirectorSales extends Actor with ActorLogging {

  for(nr <- Range(1, 3))(hire(SalesRep.props(nr), SalesRep.name(nr), (nr * 3).seconds))

  def receive = {
    case SalesRep.Quit(nr) =>
      fire(SalesRep.props(nr), SalesRep.name(nr))
      hire(SalesRep.props(nr), SalesRep.name(nr), 3.seconds)
  }
}
