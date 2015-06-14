package nworks.reporter

import java.net._
import java.util.concurrent.Executors

import akka.actor.ActorRef
import play.api.libs.json.{Json, JsValue}

object UdpReporter {


  val group = InetAddress.getByName("228.5.6.7");
  val remote: InetSocketAddress = new InetSocketAddress(group, 9003)

  val clientSocket = new DatagramSocket()

  val executorService = Executors.newSingleThreadExecutor();

  def send(event: AkkaTreeEvent): Unit = {
    executorService.execute(new Runnable {
      override def run(): Unit = {
        println("!!!!! " + Json.stringify(event.json))
        val bytes = Json.stringify(event.json).getBytes
        clientSocket.send(new DatagramPacket(bytes, bytes.length, remote));
      }
    });
  }
}

sealed trait AkkaTreeEvent {
  def ref: ActorRef
  def event: String

  val hostname = System.getProperty("akkatree.hostname", InetAddress.getLocalHost.getHostName)

  def json: JsValue = Json.obj(
    "actorpath" -> ref.path.toString,
    "host" -> hostname,
    "event" -> Json.obj("type" -> event)
  )
}

case class ActorCreated(ref: ActorRef) extends AkkaTreeEvent {
  val event = "started"
}

case class ActorRemoved(ref: ActorRef) extends AkkaTreeEvent {
  val event = "terminated"
}

case class MessageSend(from: ActorRef, to: ActorRef) extends AkkaTreeEvent {
  def ref = from
  val event = "message"

  override def json: JsValue = Json.obj(
    "from" -> from.path.toString,
    "to" -> to.path.toString,
    "event" -> Json.obj("type" -> event)
  )
}

