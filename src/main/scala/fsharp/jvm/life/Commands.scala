package fsharp.jvm.life

sealed trait Command {}

case class Go() extends Command
case class Stop() extends Command
case class Reset() extends Command
case class Quit() extends Command
case class MouseDown(x: java.lang.Integer, y: java.lang.Integer) extends Command
case class MouseUp(x: java.lang.Integer, y: java.lang.Integer) extends Command
case class ScrollViewport(x: java.lang.Integer, y: java.lang.Integer) extends Command
