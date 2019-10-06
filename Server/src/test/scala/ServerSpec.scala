import akka.actor.{ActorSelection, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import commons._
import org.scalatest.WordSpecLike
import server.ServerActor

import scala.concurrent.duration._
import server.GridBombUtils._
import TestUtils._

class ServerSpec extends TestKit(testSystem)
  with WordSpecLike
  with StopSystemAfterAll
  with ImplicitSender {

  "ServerActor" must {
    "send response for person death after completion of grenade lifecycle" in {
      val temp: ActorSelection = testSystem.actorSelection(testActor.path)
      val serverActor = TestActorRef[ServerActor](Props(classOf[ServerActor], Some(temp)))

      serverActor ! Start
      serverActor ! Message("(1,1,-1);(2,2,1)")
      expectMsg(15 seconds, Message(PERSON_KILLED))
    }

    "send unable to spawn response in case of same person coordinate input" in {
      val temp: ActorSelection = testSystem.actorSelection(testActor.path)
      val serverActor = TestActorRef[ServerActor](Props(classOf[ServerActor], Some(temp)))

      serverActor ! Start
      serverActor ! Message("(1,1,-1)")
      serverActor ! Message("(1,1,-1)")
      expectMsg(5 seconds, Message(UNABLE_TO_SPAWN))
    }

    "send person death response if spawned inside grenade area" in {
      val temp: ActorSelection = testSystem.actorSelection(testActor.path)
      val serverActor = TestActorRef[ServerActor](Props(classOf[ServerActor], Some(temp)))

      serverActor ! Start
      serverActor ! Message("(5,5,1)")
      serverActor ! Message("(5,5,-1)")
      expectMsg(15 seconds, Message(PERSON_KILLED))
    }

    "throw IllegalStateException in case of unknown event" in {
      val temp: ActorSelection = testSystem.actorSelection(testActor.path)
      val serverActor = TestActorRef[ServerActor](Props(classOf[ServerActor], Some(temp)))

      serverActor ! "DUMMY"
      assertThrows[IllegalStateException] _
    }

    "throw IllegalArgumentException in case of out of grid inputs" in {
      val temp: ActorSelection = testSystem.actorSelection(testActor.path)
      val serverActor = TestActorRef[ServerActor](Props(classOf[ServerActor], Some(temp)))

      serverActor ! Message("(10,1,-1)")
      assertThrows[IllegalArgumentException] _

      serverActor ! Message("(5,10,4)")
      assertThrows[IllegalArgumentException] _
    }
  }
}
