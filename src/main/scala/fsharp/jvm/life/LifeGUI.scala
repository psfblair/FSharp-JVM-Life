package fsharp.jvm.life

import java.awt.{Dimension, FlowLayout, BorderLayout}
import java.util.concurrent.BlockingQueue
import javax.swing._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class LifeGUI(width: Int, height: Int, queue: BlockingQueue[Command]) {
  var contentPane: JPanel = _
  private var stopButton: JButton = _
  private var goButton: JButton = _
  private var resetButton: JButton = _
  private var canvas: LifeCanvas = _

  contentPane = new JPanel()
  contentPane.setLayout(new BorderLayout(0, 0))
  val panel1 = new JPanel()
  panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5))
  contentPane.add(panel1, BorderLayout.SOUTH)
  stopButton = new JButton()
  stopButton.setText("Stop")
  panel1.add(stopButton)
  goButton = new JButton()
  goButton.setText("Go")
  panel1.add(goButton)
  resetButton = new JButton()
  resetButton.setText("Reset")
  panel1.add(resetButton)
  canvas = new LifeCanvas()
  canvas.setBackground(RunLife.bgColor)
  canvas.setOpaque(true)
  canvas.setMinimumSize(new Dimension(width, height))
  contentPane.add(canvas, BorderLayout.CENTER)

  goButton.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent): Unit = {
      queue.put(Go())
    }
  })

  stopButton.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent): Unit = {
      queue.put(Stop())
    }
  })

  resetButton.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent): Unit = {
      queue.put(Reset())
    }
  })

  canvas.addMouseListener(new MouseAdapter() {
    override def mousePressed(e: MouseEvent) {
      queue.put(MouseDown(e.getX, e.getY))
      super.mousePressed(e)
    }

    override def mouseReleased(e: MouseEvent) {
      queue.put(MouseUp(e.getX, e.getY))
      super.mouseReleased(e)
    }

    override def mouseDragged(e: MouseEvent) {
      val targetX = e.getX
      val targetY = e.getY
      queue.put(ScrollViewport(targetX, targetY))
      super.mouseDragged(e)
    }
  })

  def clearCanvas() = canvas.clearCanvas()

  def paintCell(x: Int, y: Int, side: Int) = canvas.paintCell(x, y, side)
}