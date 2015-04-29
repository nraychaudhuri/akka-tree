package nworks.actortree.producer

import akka.actor._

object Advisor {
  def props(nr: Int): Props = Props(new Advisor(nr))

  def name(nr: Int) = s"advisor-$nr"

  case class Quit(nr: Int)
}

class Advisor(nr: Int) extends Actor with ActorLogging {
  import Advisor._

  def receive = {
    case message => log.debug(s"Actor ${name(nr)} received message: $message")
  }
}
