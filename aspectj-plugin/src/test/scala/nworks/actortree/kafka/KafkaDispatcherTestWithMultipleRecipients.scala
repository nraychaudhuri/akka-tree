package nworks.actortree.kafka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.FlowMaterializer
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.Source
import nworks.actortree.visualizer.web.FlowEventPublisher

/**
 * User: Evgeny Zhoga
 * Date: 15.11.14
 */
object KafkaDispatcherTestWithMultipleRecipients {
  def main(args: Array[String]): Unit = {
    val s = ActorSystem("foo")
    val r1 = s.actorOf(Props(new RecipientActor("r1")), "r1")
    val kafkaDispatcher = s.actorOf(KafkaDispatcher.props, "kafka-dispatcher")

    kafkaDispatcher ! Recipient(r1, "r1")
    Thread.sleep(5000l)

    val r2 = s.actorOf(Props(new RecipientActor("r2")), "r2")
    kafkaDispatcher ! Recipient(r2, "r2")
  }
}



