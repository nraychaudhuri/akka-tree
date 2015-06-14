package example

import java.io.File

import akka.actor._


object WritePrimes extends App {
  val system = ActorSystem("test-stream")

  class Actor1 extends Actor {
    val actor2 = context.actorOf(Props[Actor2], name="actor2")
    val actor3 = context.actorOf(Props[Actor3], name="actor3")

    def receive = {
      case "ping" =>
        actor2 ! "ping"
        actor3 ! "ping"
    }
  }

  class Actor2 extends Actor {
    val actor3 = context.actorOf(Props[Actor3], name="actor3")

    def receive = {
      case "ping" =>
        actor3 ! "ping"
    }
  }

  class Actor3 extends Actor {
    def receive = {
      case "ping" =>
        sender ! "pong"
        sender ! "pong"
    }
  }

  val actor1 = system.actorOf(Props[Actor1])

  (1 to 10000).foreach { i =>
    actor1 ! "ping"
    Thread.sleep(1000)
  }
}