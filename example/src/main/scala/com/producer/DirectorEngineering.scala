package nworks.actortree.producer

import akka.actor._
import scala.concurrent.duration._
import util.OrgChanger._

object DirectorEngineering {
  def props: Props = Props(new DirectorEngineering())

  val name = "director-engineering"
}

class DirectorEngineering extends Actor with ActorLogging {
  import DirectorEngineering._

  println("Director Engineering created")

  hire(ChiefArchitect.props, ChiefArchitect.name, 10.seconds)
  hire(BackendLead.props, BackendLead.name, 13.seconds)
  hire(FrontendLead.props, FrontendLead.name, 14.seconds)

  def receive = {
    case msg => log.info("msg: " + msg)
  }
}
