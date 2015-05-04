import actors.{GameController, User}
import akka.actor.{ActorRef, ActorSystem}
import akka.contrib.pattern.{ShardRegion, ClusterSharding}
import scala.collection.breakOut
import akka.cluster.Cluster

object Main {

  val Opt = """(\S+)=(\S+)""".r

  def argsToOpts(args: Seq[String]): Map[String, String] =
    args.collect { case Opt(key, value) => key -> value }(breakOut)

  def applySystemProperties(options: Map[String, String]): Unit =
    for ((key, value) <- options if key startsWith "-D")
      System.setProperty(key substring 2, value)

  def main(args: Array[String]): Unit = {
    val opts = argsToOpts(args.toList)
    applySystemProperties(opts)

    val system = ActorSystem("ClusterSystem")
    createSharding(system)

    Cluster(system) registerOnMemberUp {
      val name = opts.getOrElse("name", "akkatree")
      val count = opts.getOrElse("count", "5").toInt
      val gameController= system.actorOf(GameController.props, "controller")
      for(i <- 1 to count) {
        gameController ! GameController.CreateNewUser(name + i)
      }
    }

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        println(">>>>>> shutting down the actor system")
        system.shutdown()
        system.awaitTermination()
      }
    })
  }

  private def createSharding(system: ActorSystem) = {
    ClusterSharding(system).start(
      typeName = User.shardName,
      entryProps = Some(User.props),
      idExtractor = User.idExtractor,
      shardResolver = User.shardResolver
    )
  }


}
