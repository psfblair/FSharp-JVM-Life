# Demo: FSharp on the JVM

This project contains a Conway Game of Life written in F#
and running on the JVM.

There is a .sln file for editing and building the F# project.
The build uses WebSharper to compile to Javascript; the output
goes into `src/main/resources`, which is the resources directory
for the Scala GUI application. 

The JVM code uses the Nashorn Javascript runtime to run the code.

The Scala jar file may be built with Gradle. Run:

    gradle clean build uberjar

and then

    java -jar build/libs/fsharp-jvm-life-1.0-SNAPSHOT.jar

or one of the run scripts in the root directory.
