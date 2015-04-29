package nworks.actortree.producer

import akka.actor._
import scala.concurrent.duration._
import util.OrgChanger._

object CEO {
  def props: Props = Props(new CEO())

  val name = "ceo"
}

class CEO extends Actor with ActorLogging {
  import context.dispatcher
  import CEO._

  println(s"${CEO.name} founded the startup")

  hire(DirectorEngineering.props, DirectorEngineering.name, 5.second)

  hire(DirectorSales.props, DirectorSales.name, 8.second)
  hire(DirectorMarketing.props, DirectorMarketing.name, 10.second)
  hire(AdvisoryBoard.props, AdvisoryBoard.name, 15.second)

  def receive = {
    case message => log.debug(s"Actor $name received message: $message")
  }
}
