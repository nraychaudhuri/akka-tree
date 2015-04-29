package nworks.actortree.kafka

import akka.actor.Actor

/**
 * User: Evgeny Zhoga
 * Date: 15.11.14
 */
class RecipientActor(clientId: String) extends Actor {
   var counter = 0
   override def receive: Receive = {
     case KafkaMessage(m) =>
       counter += 1
   }
 }
