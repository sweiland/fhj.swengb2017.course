package at.fhj.swengb.apps.maze

import java.lang

import scala.collection.JavaConverters._

/**
  * Encodes conversions between business models and protocol encodings
  */
object MazeProtocol {

  /**
    * Provided a protobuf encoded maze, create a business model class 'maze' again
    */
  // TODO implement missing functionality
  def convert(protoMaze: MazeProtobuf.Maze): Maze = {
    println(protoMaze.getSizeX)
    println(protoMaze.getSizeY)
    Maze(
      protoMaze.getSizeX,
      protoMaze.getSizeY,
      Pos(0, 0), // TODO read from proto
      Pos(1, 0), // TODO read from proto
      Array.fill(protoMaze.getSizeX * protoMaze.getSizeY)(Cell(Pos(0, 0), Coord(0, 0), Rect(0, 0))), // TODO read from proto
      Rect(100, 100) // TODO read from proto
    )
  }

  /**
    * Convert a business model maze to a protocol encoded maze
    */
  def convert(maze: Maze): MazeProtobuf.Maze = {
    val pCoord = MazeProtobuf.Coord.newBuilder().build

    val cells: lang.Iterable[MazeProtobuf.Cell] = maze.grid.map(convert).toIterable.asJava

    val pMaze = MazeProtobuf.Maze.newBuilder()
      .setSizeX(maze.sizeX)
      .setSizeY(maze.sizeY)
      .setStart(convert(maze.start))
      .setEnd(convert(maze.end))
      .addAllGrid(cells)
      .setCellRect(convert(maze.cellRect))
      .build()
    pMaze
  }

  def convert(end: Pos): MazeProtobuf.Pos = MazeProtobuf.Pos.newBuilder().setX(end.x).setY(end.y).build()

  def convert(cellRect: Rect): MazeProtobuf.Rect = MazeProtobuf.Rect.newBuilder().setWidth(cellRect.width).setHeight(cellRect.height).build()

  def convert(cell: Cell): MazeProtobuf.Cell = {
    val bldr = MazeProtobuf.Cell.newBuilder()

    val someUp = cell.up
    someUp match {
      case Some(upPos) => bldr.setUp(convert(upPos))
      case None => bldr.setNoneUp(true)
    }

    val someDown = cell.down
    someDown match {
      case Some(downPos) => bldr.setDown(convert(downPos))
      case None => bldr.setNoneDown(true)
    }

    bldr.setPos(MazeProtobuf.Pos.newBuilder().setX(cell.pos.x).setY(cell.pos.y))
    bldr.setTopLeft(MazeProtobuf.Coord.newBuilder().setX(cell.topLeft.x).setY(cell.topLeft.y))
    bldr.setRegion(MazeProtobuf.Rect.newBuilder().setWidth(cell.width).setHeight(cell.height))
    bldr.build()
  }

  def convert(cell: MazeProtobuf.Cell): Cell = {
    val someUp = if (cell.getNoneUp) None else Option(convert(cell.getUp))
    Cell(null, null, null, someUp, None, None, None)
  }

  def convert(pos: MazeProtobuf.Pos): Pos = Pos(MazeProtobuf.Pos.X_FIELD_NUMBER, MazeProtobuf.Pos.Y_FIELD_NUMBER)
}
