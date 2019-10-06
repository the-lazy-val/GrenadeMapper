package commons

trait Event
case object Start extends Event
case object Tick extends Event
case object PersonDead extends Event
case object InvalidCoordinates extends Event
case class Message(msg: String) extends Event
