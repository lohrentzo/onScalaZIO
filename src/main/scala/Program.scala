// https://skillsmatter.com/skillscasts/13247-scala-matters#video

import zio.ZIO
import java.io.IOException
import zio.DefaultRuntime

trait Console {
  def console: Console.Service
}

object Console {
  trait Service {
    def printLine(line: String): ZIO[Any, Nothing, Unit]
    val readLine: ZIO[Any, IOException, String]
  }
}

// implementation for the IO Console
object ConsoleLive extends Console.Service {
  def printLine(line: String): ZIO[Any, Nothing, Unit] =
    ZIO.effectTotal(scala.Console.println(line))
    
  val readLine: ZIO[Any, IOException, String] =
    ZIO.effect(scala.io.StdIn.readLine()).refineOrDie {
    case e : IOException => e
  }
}

// helpers to avoid having to call accessM all the time
package object console {
  def printLine(line: String) : ZIO[Console, Nothing, Unit] =
    ZIO.accessM(_.console printLine line)
    
   val readLine: ZIO[Console, IOException, String] =
     ZIO.accessM(_.console.readLine)
}
  

object Program extends App {
 
// build the program as a functional effect
val program: ZIO[Console, IOException, Unit] =
  for {
    _    <- printLine("Good morning, what is your name?")
    name <- readLine
    _    <- printLine(s"Nice to meet you, $name!")
  } yield ()

 DefaultRuntime.unsafeRun(program.provide(ConsoleLive))
  
}