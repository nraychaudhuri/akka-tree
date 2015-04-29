package nworks.actortree.producer

import akka.actor._
import util.OrgChanger._
import scala.concurrent.duration._
import scala.util.Random

object MarketingEmployee {
  def props(nr: Int): Props = Props(new MarketingEmployee(nr))

  def name(nr: Int) = s"marketing-employee-$nr"

  case class Quit(nr: Int)
}

class MarketingEmployee(nr: Int) extends Actor with ActorLogging {
  import MarketingEmployee._

  val quitInSeconds = 5 + Random.nextInt(25)
  quit(Quit(nr), quitInSeconds.seconds)

  def receive = {
    case message => log.debug(s"Actor ${name(nr)} received message: $message")
  }
}
