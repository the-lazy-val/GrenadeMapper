package commons

trait Event
case object Start extends Event
case object Tick extends Event
case class Message(msg: String) extends Event
