package fsharp.jvm.life

import java.awt.Color
import java.io.InputStreamReader
import javax.script.{Invocable, ScriptEngine, ScriptEngineManager}
import javax.swing.JFrame
import java.util.concurrent.{LinkedBlockingQueue, BlockingQueue}

object RunLife {
  private val setTimeoutPolyfillPath = "/settimeout-nashorn.js"
  private val lifeJsPath = "/FSharp_JVM_Life.js"
  val width = 500
  val height = 500
  val minButtonHeight = 50
  val cellSide = 8
  val xOffset = 0
  val yOffset = 0
  val bgColor = Color.CYAN
  val lineColor = Color.BLACK
  val lineWidth = 2

  private var engine: ScriptEngine = _
  private var invoker: Invocable = _
  private var lifeGui: LifeGUI = _
  private val queue: BlockingQueue[Command] = new LinkedBlockingQueue[Command]()

  def main(args: Array[String]): Unit = {
    startGui(queue)
    loadJs()
    initializeGame()
    processCommands(queue)
  }

  def startGui(queue: BlockingQueue[Command]) = {
    val frame = new JFrame("fsharp.jvm.life.LifeGUI")
    frame.setSize(width, height + minButtonHeight)
    lifeGui = new LifeGUI(width, height, queue)
    frame.setContentPane(lifeGui.contentPane)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setVisible(true)
  }

  def loadJs() = {
    // Normally would have this in a try block. eval-ing a function throws ScriptException.
    val factory = new ScriptEngineManager()
    engine = factory.getEngineByName("nashorn")
    invoker = engine.asInstanceOf[Invocable]
    engine.eval(new InputStreamReader(getClass.getResourceAsStream(setTimeoutPolyfillPath)))
    engine.eval(new InputStreamReader(getClass.getResourceAsStream(lifeJsPath)))
  }

  def initializeGame(): Unit = {
    val initializeJs =
      s"""
        | var runner = Java.type("fsharp.jvm.life.RunLife");
        | FSharp_JVM_Life.Canvas.initialize(
        |    $width, $height, $cellSide, $xOffset, $yOffset, runner.clearCanvas, runner.drawCell
        | );
      """.stripMargin
    engine.eval(initializeJs)
    lifeGui.clearCanvas()
  }

  // Js global context is thread local - the AWT thread coming from the mouse click
  // can't see it. So we use the blocking queue to have this thread do the work.
  def processCommands(queue: BlockingQueue[Command]): Unit = {
    Thread.sleep(100)
    val command = queue.take()
    command match {
      case Go() => go(); processCommands(queue)
      case Stop() => stop(); processCommands(queue)
      case Reset() => reset(); processCommands(queue)
      case Quit() => ()
      case MouseDown(x, y) => mouseDown(x, y); processCommands(queue)
      case MouseUp(x, y) => mouseUp(x, y); processCommands(queue)
      case ScrollViewport(x, y) => scrollViewport(x, y); processCommands(queue)
    }
  }

  def go(): Unit = {
    engine.eval(s"FSharp_JVM_Life.Canvas.go();")
  }

  def stop(): Unit = {
    engine.eval(s"FSharp_JVM_Life.Canvas.stop();")
  }

  def reset(): Unit = {
    engine.eval(s"FSharp_JVM_Life.Canvas.reset();")
  }

  def mouseDown(x: java.lang.Integer, y: java.lang.Integer): Unit = {
    engine.eval(s"FSharp_JVM_Life.Canvas.setMouseDown($x,$y);")
  }

  def mouseUp(x: java.lang.Integer, y: java.lang.Integer): Unit = {
    engine.eval(s"FSharp_JVM_Life.Canvas.setMouseUp($x,$y);")
  }

  def scrollViewport(x: java.lang.Integer, y: java.lang.Integer): Unit = {
    engine.eval(s"FSharp_JVM_Life.Canvas.scrollViewport($x,$y);")
  }

  def clearCanvas(jsUndefined: Any) = {
    lifeGui.clearCanvas()
  }

  def drawCell(xyCoordinates: Array[Int]) = {
    lifeGui.paintCell(xyCoordinates(0), xyCoordinates(1), cellSide)
  }
}