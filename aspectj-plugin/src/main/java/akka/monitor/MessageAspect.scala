package akka.monitor

import akka.actor._
import akka.dispatch.{ Envelope, MessageDispatcher }
import akka.routing.RoutedActorCell
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation._
import nworks.reporter._

@Aspect
class MessageAspect {
  @Pointcut("(execution(* akka.actor.ActorCell.invoke(*)) || execution(* akka.routing.RoutedActorCell.sendMessage(*))) && this(cell) && args(envelope)")
  def invokingActorBehaviourAtActorCell(cell: ActorCell, envelope: Envelope) = {}

  @Around("invokingActorBehaviourAtActorCell(cell, envelope)")
  def aroundBehaviourInvoke(pjp: ProceedingJoinPoint, cell: ActorCell, envelope: Envelope): Any = {
    UdpReporter.send(new MessageSend(envelope.sender, cell.self));
    pjp.proceed()
  }
}
