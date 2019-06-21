import scalaz.zio._
import scalaz.zio.console._
import java.io.IOException
import scala.util._


object Hangman extends App {
  
  case class State(name: String, guesses: Set[Char] = Set.empty[Char], word: String) {
    final def failures: Int = (guesses -- word.toSet).size
    final def playerLost: Boolean = failures >= 10
    final def playerWon: Boolean = (word.toSet -- guesses).size == 0
  }
  
  //lazy val wordDictionary: List[String] = Try(scala.io.Source.fromResource("words.txt")) match {
  lazy val wordDictionary: List[String] = Try(scala.io.Source.fromFile("words.txt")) match {
    case Success(source) => source.getLines().toList
    case Failure(f) => List("error")
  }
  
  def run(args: List[String]) : IO[Nothing, ExitStatus] = {
    hangman.redeemPure(
        _ => ExitStatus.ExitNow(1),
        _ => ExitStatus.ExitNow(0))
  }
  
  val hangman: IO[IOException, Unit] = for {
    _    <- putStrLn("Welcome to purely functional hangman")
    name <- getName
    _    <- putStrLn(s"Welcome $name. Let's begin!")
    word <- chooseWord
    state = State(name, Set(), word)
    _    <- renderState(state)
    _    <- gameLoop(state)
  } yield ()  
  
//  val getName : IO[IOException, String] = for {
//    _ <- putStrLn("What is your name?")
//    name <- getStrLn
//  } yield name
  
  val getName : IO[IOException, String] = putStrLn("What is your name?") *> getStrLn
    
  val getChoice: IO[IOException, Char] = for {
      line <- putStrLn(s"Please enter a letter") *> getStrLn
      char <- line.toLowerCase.trim.headOption match {
        case None    => putStrLn(s"You did not enter a valid character") *> getChoice
        case Some(x) => IO.now(x) 
      }
    } yield char

  def nextInt(max: Int): IO[Nothing, Int] = IO.sync(scala.util.Random.nextInt(max))
  
  val chooseWord: IO[IOException, String] = for {
    rand <- nextInt(wordDictionary.length)
  } yield (wordDictionary.lift(rand).getOrElse("Bug in the program!"))
  

  def renderState(state: State) : IO[IOException, Unit] = {
    val word = state.word.toList.map(c => 
        if (state.guesses.contains(c)) s" $c " else " "
    ).mkString("")
    val line = List.fill(state.word.length)(" - ").mkString("")
    val guesses = " Guesses: " + state.guesses.toList.sorted.mkString("")
    val text = word + "\n" + line + "\n\n" + guesses + "\n"
    putStrLn(text)
  }
  
  def gameLoop(state: State) : IO[IOException, State] = {
    for {
      guess <- getChoice
      state <- IO.now(state.copy(guesses = state.guesses + guess))
      _     <- renderState(state)
      loop  <- if (state.playerWon) putStrLn(s"Congratulations ${state.name}, you won the game").const(false)
               else if (state.playerLost) putStrLn(s"Sorry ${state.name}, you lost the game").const(false)
               else if (state.word.contains(guess)) putStrLn(s"You guessed correctly!").const(true)
               else putStrLn(s"That's wrong. but keep trying!").const(true)
      state <- if (loop) gameLoop(state) else IO.now(state)
    } yield state
  }
}