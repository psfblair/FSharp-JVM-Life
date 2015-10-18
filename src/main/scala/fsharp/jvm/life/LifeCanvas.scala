package fsharp.jvm.life

import java.awt._
import javax.swing.JPanel

class LifeCanvas extends JPanel{
  sealed trait PaintCommand {}
  case class PaintCells(cells: Set[(Int,Int,Int)]) extends PaintCommand
  case class ClearCanvas() extends PaintCommand
  case class ShowLoadingMessage() extends PaintCommand
  case class ShowWelcomeMessage() extends PaintCommand

  var command : PaintCommand = ShowLoadingMessage()

  def paintCell(x: Int, y: Int, side: Int) = {
    command match {
      case PaintCells(cells) => {
        val newCell: (Int,Int,Int) = (x,y,side)
        command = PaintCells(cells + newCell)
      }
      case _ => command = PaintCells(Set((x,y,side)))
    }
    repaint()
  }

  def clearCanvas() = {
    command = ClearCanvas()
    repaint()
  }

  def showWelcomeMessage() = {
    command = ShowWelcomeMessage()
    repaint()
  }

  override def paintComponent(g: Graphics) {
    super.paintComponent(g)
    command match {
      case ShowLoadingMessage() =>
        val g2 = g.asInstanceOf[Graphics2D]
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24))
        g2.drawString("Loading game from F#...", RunLife.width / 5, RunLife.height / 3)
      case ShowWelcomeMessage() =>
        val g2 = g.asInstanceOf[Graphics2D]
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18))
        g2.drawString("Click in the canvas to toggle cells.", RunLife.width / 5, RunLife.height / 3)
      case ClearCanvas() =>
        g.setColor(RunLife.bgColor)
        g.fillRect(0, 0, getWidth, getHeight)
      case PaintCells(cells) =>
        cells.foreach((triple: (Int,Int,Int)) => {
          val x = triple._1
          val y = triple._2
          val side = triple._3
          g.setColor(RunLife.bgColor)
          g.fillRect(x,y,side,side)
          g.setColor(RunLife.lineColor)
          val g2 = g.asInstanceOf[Graphics2D]
          g2.setStroke(new BasicStroke(RunLife.lineWidth))
          g.drawOval(x, y, side, side)
          ()
        })
    }
  }
}
