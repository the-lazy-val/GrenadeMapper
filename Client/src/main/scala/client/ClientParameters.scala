package client

import com.typesafe.config.ConfigFactory

case class ClientParameters(gridWidth: Int, gridLength: Int, maxBombs:Int, maxBombRadius: Int, tickInterval: Int)

object ClientParameters{
  def fromConfig = {
    val config = ConfigFactory.load().getConfig("client.params")

    val width = config.getInt("grid_width")
    val length = config.getInt("grid_length")
    val maxRadius = config.getInt("max_bomb_radius")
    val maxBombs = config.getInt("max_num_bombs")
    val tickInterval = config.getInt("tick_interval_seconds")

    ClientParameters(width, length, maxBombs, maxRadius, tickInterval)
  }
}