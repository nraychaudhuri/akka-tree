package nworks.actortree.producer.util

import akka.actor._
import scala.concurrent.duration._

object OrgChanger {
  def hire(props: Props, name: String, duration: FiniteDuration)(implicit context: ActorContext): Unit = {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(duration) {
      context.actorOf(props, name)
      println(s"$name hired")
    }
  }

  def fire(props: Props, name: String)(implicit context: ActorContext): Unit = {
    context.child(name).map {
      ref => context.stop(ref)
      println(s"$name fired")
    }

  }

  def quit(message: Any, duration: FiniteDuration)(implicit context: ActorContext): Unit = {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(duration)(context.parent ! message)
  }
}
