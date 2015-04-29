package nworks.actortree.producer

import akka.actor._
import util.OrgChanger._
import scala.concurrent.duration._
import scala.util.Random

object BackendDeveloper {
  def props(nr: Int): Props = Props(new BackendDeveloper(nr))

  def name(nr: Int) = s"backend-developer-$nr"

  case class Quit(nr: Int)
}

class BackendDeveloper(nr: Int) extends Actor with ActorLogging {
  import BackendDeveloper._

  println("Backend Developer created")

  val quitInSeconds = 2 + Random.nextInt(18)
  quit(Quit(nr), quitInSeconds.seconds)

  def receive = {
    case message => log.debug(s"Actor ${name(nr)} received message: $message")
  }
}
