package server

import akka.actor._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import GridBombUtils._
import commons._

object Server extends App {
  implicit val system = ActorSystem("ServerSystem")
  val serverActor = system.actorOf(Props(classOf[ServerActor], None), name = "ServerActor")
  serverActor ! Start
}

class ServerActor(optClient: Option[ActorSelection]=None) extends Actor with ActorLogging {

  val params = ServerParameters.fromConfig
  implicit val grid = Grid(params.gridWidth, params.gridLength)
  val client = optClient.getOrElse(context.actorSelection("akka.tcp://ClientSystem@127.0.0.1:5150/user/ClientActor"))

  override def receive: Receive = updateState(GridState(Grid(params.gridWidth, params.gridLength), Set.empty, None, Set.empty, false))

  private def updateState(gridState: GridState): Receive = {
    case Start => {
      log.info("Server started...")
      context.system.scheduler.schedule(
        0 milliseconds,
        params.tickInterval seconds,
        self,
        Tick)
    }

    case Tick => {
      gridState.grenades.find(g => g.killedPerson && g.radius== -1).map(g =>(1 to g.killCount).map(_ => self ! PersonDead))

      val activeGrenadesState = gridState.removeInactiveGrenades
      activeGrenadesState.printGridState

      val nextState =
        if(activeGrenadesState.checkPersonDeath) nextStep(activeGrenadesState.updateGridForDeadPerson) else nextStep(activeGrenadesState)
      context.become(updateState(nextState))
    }

    case PersonDead => {
      client ! Message(PERSON_KILLED)
    }

    case InvalidCoordinates => {
      client ! Message(UNABLE_TO_SPAWN)
    }

    case Message(msg) => {
      val input = msg.split(";").map(parseToTuple).toList
      val personInput = input.find(_._3 == -1).map(t => Point.init(t._1, t._2))
      val personAlreadyExists = personInput.flatMap(pi => gridState.person.map(_ == pi)).getOrElse(false)


      //TODO: if the client sends a PERSON coordinate and at-least 1 grenade is in explosion state, with an already alive person, then shall that person move?
      val personUpdate =
        if(personAlreadyExists && !gridState.isPersonDead){
          self ! InvalidCoordinates
          (gridState.person, gridState.isPersonDead)
        }
        else
          personInput.map(p => (Some(p), false)).getOrElse(gridState.person, gridState.isPersonDead)

      val newGrenades = input.filter(_._3 != -1).map(t => Grenade.init(Point.init(t._1, t._2), t._3)).toSet
      val updatedGridState = gridState.copy(person = personUpdate._1, grenades = gridState.grenades ++ newGrenades, isPersonDead = personUpdate._2)
      context.become(updateState(updatedGridState))
    }

    case _ => throw new IllegalStateException(UNKNOWN_EVENT)
  }
}


