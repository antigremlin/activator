/**
 *   Copyright (C) 2012 Typesafe Inc. <http://typesafe.com>
 */
import com.typesafe.sbtchild._

import akka.actor._
import java.util.concurrent.atomic.AtomicInteger

class MockSbtChildFactory extends SbtChildFactory {
  def creator = new Actor() {
    override def receive = {
      case req: protocol.Request =>
        if (req.sendEvents)
          sender ! protocol.LogEvent(protocol.LogMessage(level = 1, message = "Hello!"))
        req match {
          case protocol.NameRequest(_) =>
            sender ! protocol.NameResponse("foo")
          case protocol.CompileRequest(_) =>
            sender ! protocol.CompileResponse(success = true)
          case protocol.RunRequest(_) =>
            sender ! protocol.RunResponse(success = true)
        }
    }
  }

  val childNum = new AtomicInteger(1)

  override def newChild(actorFactory: ActorRefFactory): ActorRef = {
    actorFactory.actorOf(Props(creator = creator), "mock-sbt-child-" + childNum.getAndIncrement())
  }
}