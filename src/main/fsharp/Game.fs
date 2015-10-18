namespace FSharp_JVM_Life

open WebSharper

[<JavaScript>]
module Game =
   open Core

(* ***************** State management ***************** *)

   let mutable currentState = Set.empty<Cell>

   let isAlive cell = 
      Set.contains cell currentState

   let removeCell cell =
      currentState <- Set.remove cell currentState

   let addCell cell =
      currentState <- Set.add cell currentState

   let toggleCell cell =
      if isAlive cell then
         removeCell cell
      else 
         addCell cell

   let resetAllCells = Set.empty<Cell>
   
   let mutable stop = false

   let rec generationLoop drawFunction =
      if stop then
            async { return () }
      else
            async {
               do! Async.Sleep 500
               let newState = nextGeneration currentState
               do  drawFunction newState
               do! generationLoop drawFunction
            }
  
   let startDrawing drawFunction =
      stop <- false
      Async.Start (generationLoop drawFunction)

   let stopDrawing () =
      stop <- true

   let stopDrawingAndReset drawFunction =
      stopDrawing ()
      currentState <- resetAllCells 
      drawFunction currentState

   let toggleCellAndRedraw (x,y) drawFunction =
      let newState = toggleCell (x,y)
      drawFunction currentState