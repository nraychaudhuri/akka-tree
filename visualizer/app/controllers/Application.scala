package controllers

import java.net.{InetAddress, MulticastSocket, DatagramPacket, DatagramSocket}

import scala.concurrent.Future

import play.api.http.MimeTypes
import play.api.libs.EventSource
import play.api.libs.iteratee.{Enumeratee, Enumerator}
import play.api.mvc._

object Application extends Controller {

  val serverSocket = new MulticastSocket(9003)
  val group = InetAddress.getByName("228.5.6.7");
  serverSocket.joinGroup(group);

  def index = Action {
    Ok(views.html.index())
  }

  def applyFilter = Action {
    Redirect(routes.Application.index())
  }

  def events = Action {
    Ok.chunked(userEvents().through(EventSource())).as(MimeTypes.EVENT_STREAM)
  }

  private def userEvents(): Enumerator[String] = {
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    //TODO: Use akka.io for udp. This is blocking right now
    Enumerator.repeatM(
      Future {
        val receiveData = new Array[Byte](2048);
        val receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        new String(receivePacket.getData, 0, receivePacket.getLength)
    }).through(Enumeratee.filter(s => s.contains("user")))
  }

}