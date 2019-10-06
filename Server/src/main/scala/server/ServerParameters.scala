package server

import com.typesafe.config.ConfigFactory

case class ServerParameters(gridWidth: Int, gridLength: Int, tickInterval: Int)

object ServerParameters{
  def fromConfig = {
    val config = ConfigFactory.load().getConfig("server.params")

    val width = config.getInt("grid_width")
    val length = config.getInt("grid_length")
    val tickInterval = config.getInt("tick_interval_seconds")

    ServerParameters(width, length, tickInterval)
  }
}