package client

import akka.actor._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random
import commons._

object Client extends App  {
  val system = ActorSystem("ClientSystem")
  val clientActor = system.actorOf(Props[ClientActor], name = "ClientActor")
  clientActor ! Start
}

class ClientActor extends Actor with ActorLogging {
  val params = ClientParameters.fromConfig
  val server = context.actorSelection("akka.tcp://ServerSystem@127.0.0.1:5151/user/ServerActor")

  def generateBomb(r: Random, num: Int): String = (1 to num).map(_ => s"(${r.nextInt(params.gridWidth) + 1},${r.nextInt(params.gridLength) + 1},${r.nextInt(params.maxBombRadius) + 1})").mkString(";")

  def generatePerson(r: Random): String = s"(${r.nextInt(params.gridWidth) + 1},${r.nextInt(params.gridLength) + 1},-1)"

  def generateMessage(r: Random): String = {
    if(r.nextBoolean())
      generateBomb(r, r.nextInt(params.maxBombs)+1)
    else
      generatePerson(r)
  }

  def receive = {
    case Start => {
      log.info("Client Started !!!!")
      context.system.scheduler.schedule(
        0 milliseconds,
        params.tickInterval seconds,
        self,
        Tick)
    }
    case Tick => {
      val r = scala.util.Random
      val cond = r.nextBoolean()
      if(cond){
        val msg = generateMessage(r)
        println(s"sending message: $msg")
        server ! Message(msg)
      }
    }
    case Message(msg) => {
      println(s"ClientActor received message '$msg'")
    }
  }
}


