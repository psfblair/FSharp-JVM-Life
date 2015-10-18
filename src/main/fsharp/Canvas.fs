namespace FSharp_JVM_Life

open WebSharper

[<JavaScript>]
module Canvas =
  open Core
  open Game
  open System

  let mutable cellSide = 0

  let mutable canvasWidth = 0
  let mutable canvasHeight = 0

  let mutable canvasXOffset = 0
  let mutable canvasYOffset = 0

  let mutable cellXOffset = 0
  let mutable cellYOffset = 0

  let mutable previousCanvasCoordinates = None
  let mutable mouseDownCanvasCoordinates = None

  let mutable clearCanvas: unit -> unit = fun () -> () 
  let mutable drawCell: float * float -> unit = fun (x,y) -> () 

  type CanvasCoordinatePair = float * float

  let pairMap func pair = (func (fst pair)), (func (snd pair))

  let pairMap2 func secondPair firstPair = (func (fst firstPair) (fst secondPair)), (func (snd firstPair) (snd secondPair))

  let transformCellCoordinatesToCanvasCoordinates (cell: Cell) : CanvasCoordinatePair = 
     let multiplyByCellSide element = element * cellSide
     let addHalfACellSide element = element + (cellSide / 2)

     cell |> pairMap2 (-) (cellXOffset, cellYOffset)
          |> pairMap multiplyByCellSide
          |> pairMap addHalfACellSide
          |> pairMap float

  let transformPixelPairToCellPair (pair: CanvasCoordinatePair) : Cell = 
     let divideByCellSide element = element / (float cellSide)
     pair |> pairMap divideByCellSide |> pairMap int

  let cellIsWithinViewableCanvas (canvasCoordinatePair: CanvasCoordinatePair) =
    let isBetweenOriginAnd (thePoint: float) (theLimit: int) = (thePoint > 0.0) && (thePoint < (float theLimit))
    let (xOk, yOk) = canvasCoordinatePair |> pairMap2 isBetweenOriginAnd (canvasWidth, canvasHeight)
    xOk && yOk

  let canvasCoordinatesFromMouseCoordinates (clientXYPair: int * int) : CanvasCoordinatePair =
     clientXYPair |> pairMap2 (-) (canvasXOffset, canvasYOffset) |> pairMap float

  let drawLife (gameState: Set<int*int>) =
     clearCanvas ()

     gameState |> Set.map transformCellCoordinatesToCanvasCoordinates
               |> Set.filter cellIsWithinViewableCanvas 
               |> Set.iter drawCell      

  let pixelsMovedDuring (clientXYPair: int * int) (previous: option<CanvasCoordinatePair>) : CanvasCoordinatePair = 
     match previous with
        | None -> (0.0, 0.0)
        | Some(prevX,prevY) -> canvasCoordinatesFromMouseCoordinates clientXYPair |> pairMap2 (-) (prevX, prevY)

  let didNotMoveDuringClick (clientXYPair: int * int) = 
     let withinMoveTolerance distanceMoved = (abs distanceMoved) < (float cellSide)
     let (didNotMoveX, didNotMoveY) = pixelsMovedDuring clientXYPair mouseDownCanvasCoordinates |> pairMap withinMoveTolerance
     didNotMoveX && didNotMoveY

  let adjustInitArrayAndRedraw (clientXYPair: int * int) =
     if didNotMoveDuringClick clientXYPair then
        let cell = canvasCoordinatesFromMouseCoordinates clientXYPair 
                    |> transformPixelPairToCellPair
                    |> pairMap2 (+) (cellXOffset, cellYOffset)
        toggleCellAndRedraw cell drawLife
     else
        ()

  let updateCellOffsets offsetPair =
     cellXOffset <- cellXOffset - (fst offsetPair)
     cellYOffset <- cellYOffset - (snd offsetPair)

  (****************** External Interface ***************************)

  let initialize width height cellSidePixels canvasXOffsetPixels canvasYOffsetPixels clearCanvasFn drawCellFn =         
     canvasWidth  <- width
     canvasHeight <- height
     cellSide <- cellSidePixels
     canvasXOffset <- canvasXOffsetPixels
     canvasYOffset <- canvasYOffsetPixels
     clearCanvas <- clearCanvasFn
     drawCell <- drawCellFn 

     drawLife currentState

  let go = startDrawing drawLife

  let stop = stopDrawing ()

  let reset = stopDrawingAndReset drawLife

  let setMouseDown (clientXYPair: int * int) = 
     let canvasCoordinates = canvasCoordinatesFromMouseCoordinates clientXYPair
     previousCanvasCoordinates <- Some(canvasCoordinates)
     mouseDownCanvasCoordinates <- Some(canvasCoordinates)

  let setMouseUp (clientXYPair: int * int) =
     adjustInitArrayAndRedraw clientXYPair
     previousCanvasCoordinates <- None
     mouseDownCanvasCoordinates <- None

  let scrollViewport (clientXYPair: int * int) = 
     match previousCanvasCoordinates with
        | None -> ()
        | Some(prevX,prevY) as previous ->
            pixelsMovedDuring clientXYPair previous |> transformPixelPairToCellPair |> updateCellOffsets
            previousCanvasCoordinates <- Some <| canvasCoordinatesFromMouseCoordinates clientXYPair              
            drawLife currentState
