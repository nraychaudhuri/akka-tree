package nworks.actortree.producer

import akka.actor._
import util.OrgChanger._
import scala.concurrent.duration._
import scala.util.Random

object FrontendDeveloper {
  def props(nr: Int): Props = Props(new FrontendDeveloper(nr))

  def name(nr: Int) = s"frontend-developer-$nr"

  case class Quit(nr: Int)
}

class FrontendDeveloper(nr: Int) extends Actor with ActorLogging {
  import FrontendDeveloper._

  println("Frontend Developer created")

  val quitInSeconds = 2 + Random.nextInt(8)
  quit(Quit(nr), quitInSeconds.seconds)

  def receive = {
    case message => log.debug(s"Actor ${name(nr)} received message: $message")
  }
}
