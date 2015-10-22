# Demo: FSharp on the JVM

This project contains a Conway Game of Life written in F#
and running on the JVM.

There is a .sln file for editing and building the F# project.
The build uses WebSharper to compile to Javascript; the output
goes into `src/main/resources`, which is the resources directory
for the Scala GUI application. 

The JVM code uses the Nashorn Javascript runtime to run the code.

## What I learned

If you look at the code, you'll see that the interaction between
F# and Scala is going both ways. Mouse clicks and button clicks
are passed to F# by invoking Javascript functions, and the drawing
takes place by Scala passing two callbacks to the F# code -- one
for clearing the screen and one for drawing a cell.

Generally things were pretty straightforward. To call an F# function
with multiple parameters from Javascript, its parameters need to
be a tuple.  The only tricky case was passing the no-argument
Scala callback to F# via Javascript. Running in the debugger showed
that Javascript was trying to call the `clearCanvas()` method with
an `undefined` argument; I gave `clearCanvas` a bogus parameter of type
`Any`, which solved the problem.

## Building the code

The Scala jar file may be built with Gradle. Run:

    gradle clean build uberjar

and then

    java -jar build/libs/fsharp-jvm-life-1.0-SNAPSHOT.jar

or one of the run scripts in the root directory.


