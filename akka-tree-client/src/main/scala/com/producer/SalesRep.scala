package nworks.actortree.producer

import akka.actor._
import util.OrgChanger._
import scala.concurrent.duration._
import scala.util.Random

object SalesRep {
  def props(nr: Int): Props = Props(new SalesRep(nr))

  def name(nr: Int) = s"sales-rep-$nr"

  case class Quit(nr: Int)
}

class SalesRep(nr: Int) extends Actor with ActorLogging {
  import SalesRep._

  val quitInSeconds = 2 + Random.nextInt(8)
  quit(Quit(nr), quitInSeconds.seconds)

  def receive = {
    case message => log.debug(s"Actor ${name(nr)} received message: $message")
  }
}
