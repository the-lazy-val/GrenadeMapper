package server

import com.typesafe.scalalogging.Logger

object GridBombUtils {

  val log = Logger(this.getClass)

  val PERSON_KILLED = "Person killed"
  val UNABLE_TO_SPAWN = "Unable to spawn Person in that location"
  val UNKNOWN_EVENT = "Unknown event was sent to ServerActor"

  def parseToTuple(in: String) =
    in
      .replaceAll("[()]", "")
      .split(",")
      .map(_.toInt) match {
      case Array(a, b, c) => (a, b, c)
    }

  def nextStep(gridState: GridState)={
    log.info("Next step")

    val movePersonGridState = if(gridState.grenades.exists(_.radius == 0)) gridState.jumpPerson else gridState
    val updatedGrenadeSet = movePersonGridState.grenades.filter(_.radius != -1).map(updateGrenade(_, movePersonGridState.grid))
    val updatedDangerBlocks = updatedGrenadeSet.filter(_.radius != -1).flatMap(_.computeDangerBlocksForGrenade)
    movePersonGridState.copy(grenades = updatedGrenadeSet, dangerBlocks = updatedDangerBlocks)
  }

  def updateTopLeft(topLeft: Point): Point = {
    val newX = topLeft.x - 1
    val newY = topLeft.y - 1
    val x = if(newX >= 1) newX else 1
    val y = if(newY >= 1) newY else 1

    Point(x,y)
  }

  def updateBottomRight(bottomRight: Point, grid: Grid): Point = {
    val newX = bottomRight.x + 1
    val newY = bottomRight.y + 1
    val x = if(newX <= grid.x) newX else grid.x
    val y = if(newY <= grid.y) newY else grid.y

    Point(x,y)
  }

  def updateGrenade(grenade: Grenade, grid: Grid) =
    grenade.copy(
      radius = grenade.radius-1,
      topLeft = updateTopLeft(grenade.topLeft),
      bottomRight = updateBottomRight(grenade.bottomRight, grid)
    )
}