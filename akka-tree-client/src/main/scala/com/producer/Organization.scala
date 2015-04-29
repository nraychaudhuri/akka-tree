package nworks.actortree.producer

import akka.actor.{Actor, Props, ActorSystem}
import akka.routing.RoundRobinPool
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Organization extends App {

  def start(): Unit = {
    val system = ActorSystem("organization")
    system.scheduler.scheduleOnce(5.seconds)(system.actorOf(CEO.props, CEO.name))
    
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
         println(">>>>>> shutting down the actor system")
         system.shutdown()
      }
    })
  }
  
  start()
}
