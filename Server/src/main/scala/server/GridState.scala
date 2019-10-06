package server

import com.typesafe.scalalogging.Logger

case class Grid(x: Int, y: Int)
case class Point(x: Int, y: Int)

object Point{
  def init(x: Int, y: Int)(implicit grid: Grid): Point = {
    if(x<= grid.x && y <= grid.y)
      new Point(x, y)
    else
      throw new IllegalArgumentException(s"Input co-ordinates out of Grid(${grid.x}, ${grid.y})")
  }
}

case class Grenade(origin: Point, radius: Int, topLeft: Point, bottomRight: Point, killedPerson: Boolean = false, killCount: Int = 0){
  def computeDangerBlocksForGrenade: Set[Point] = {
    val points = for{
      x <- (topLeft.x to bottomRight.x)
      y <- (topLeft.y to bottomRight.y)
    } yield Point(x,y)
    points.toSet
  }
}

object Grenade{
  def init(point: Point, radius: Int) = Grenade(point, radius, point, point)
}

case class GridState(grid: Grid, grenades: Set[Grenade], person: Option[Point], dangerBlocks: Set[Point], isPersonDead: Boolean){

  val log = Logger(this.getClass)

  def checkPersonDeath = person.map(dangerBlocks.contains(_)).getOrElse(false)

  def updateGridForDeadPerson = {
    log.info("PERSON DEAD !!!!")

    val grenadesVector = this.grenades.toVector.sortBy(_.radius)
    val idx = grenadesVector.indexWhere(g => person.map(g.computeDangerBlocksForGrenade.contains(_)).getOrElse(false))
    val killingGrenade = grenadesVector(idx)
    this.copy(grenades = grenadesVector.updated(idx, killingGrenade.copy(killedPerson = true, killCount = killingGrenade.killCount + 1)).toSet, person= None, isPersonDead = true)
  }

  def jumpPerson =
    if(isPersonDead || person.isEmpty)
      this
    else {
      log.info("JUMP PERSON")

      val newX = 1 + scala.util.Random.nextInt(grid.x)
      val newY = 1 + scala.util.Random.nextInt(grid.y)
      this.copy(person = Some(Point(newX, newY)))
    }


  def removeInactiveGrenades = {
    val activeGrenades = grenades.filter(_.radius >= 0)
    val activeDangerBlocks = activeGrenades.flatMap(_.computeDangerBlocksForGrenade)
    this.copy(grenades = activeGrenades, dangerBlocks = activeDangerBlocks)
  }

  def printGridState = {
    val gridLines = (1 to grid.y)
      .map(y => {
        (1 to grid.x)
          .map(x => {
            if (person.map(_ == Point(x,y)).getOrElse(false))
              "P"
            else if (dangerBlocks.contains(Point(x, y)))
              "X"
            else
              "-"
          })
      })
    println(gridLines.map(_.mkString(" ")).mkString("\n") + "\n")
  }
}
