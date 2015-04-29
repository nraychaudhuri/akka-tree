package nworks.actortree.kafka

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.FlowMaterializer
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.Source
import nworks.actortree.visualizer.web.FlowEventPublisher

/**
 * User: Evgeny Zhoga
 * Date: 15.11.14
 */
object KafkaDispatcherTestWithStream {
  private implicit val s = ActorSystem("bar")
  private implicit val materializer = FlowMaterializer()
  var counter = 0

  def main(args: Array[String]): Unit = {
    val s = ActorSystem("bar")

    val kafkaDispatcher = s.actorOf(KafkaDispatcher.props, "kafka-dispatcher")
    val recipientActor = createFlowEventPublisher(s)
    kafkaDispatcher ! Recipient(recipientActor, "r3")

    val source = Source(ActorPublisher[KafkaMessage](recipientActor))
    publish(source, kafkaMessageToMessage)

  }

  def publish[A](messages: Source[A], toMessage: A => Message): Unit = {
    val entity = messages.map(toMessage).foreach(m => {
      counter += 1
      println(s"<<<<< After streaming       $counter messages received --- Current message: ${m.data}")
    })

  }

  case class Message(data: String, event: Option[String] = Some("message"))

  def kafkaMessageToMessage(kafkaMessage: KafkaMessage): Message =
    Message(kafkaMessage.message)

  protected def createFlowEventPublisher(context: ActorSystem): ActorRef =
    context.actorOf(FlowEventPublisher.props)


}
