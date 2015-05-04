package monitor;

import akka.actor.ActorRef;
import nworks.reporter.ActorCreated;
import nworks.reporter.ActorRemoved;
import nworks.reporter.UdpReporter;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.JoinPoint;


import akka.actor.ActorCell;

import java.util.Optional;

@Aspect
public class MonitorAspect {

    @AfterReturning(pointcut = "execution (* akka.actor.ActorRefFactory.actorOf(..))",
            returning = "ref", argNames = "ref")
    public void actorCreationAdvice(ActorRef ref) {
        UdpReporter.send(new ActorCreated(ref));
    }

    @Pointcut(value = "execution(* akka.actor.ActorCell.stop()) && this(cell)", argNames = "cell")
    public void actorStop(ActorCell cell) {}

    @Before(value = "actorStop(cell)")
    public void beforeStop(ActorCell cell) {
        UdpReporter.send(new ActorRemoved(cell.self()));
    }

}