package com.rockthejvm.day2

import java.util.concurrent.Executors
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Random, Success, Try}

object Day2 {

  /**
    * Continue where we left off yesterday:
    * - CBN vs CBV
    * - FP problems
    */

    // passed by value
    def byValueCall(x: Long) = {
        println(s"by value: $x")
        println(s"by value: $x")
    }

    byValueCall(System.nanoTime()) // expression evaluated first
    // byValueCall(throw new RuntimeException("FAIL BY VALUE")) // <-- will crash

    // pass by name
    def byNameCall(x: => Long) = {
        println(s"by name: $x")
        println(s"by name: $x")
    }

    byNameCall(System.nanoTime()) // expression evaluated EVERY TIME when used
    // byNameCall(throw new RuntimeException("FAIL")) // will not crash


  /**
    * Pattern matching
    *   - on values, objects and case classes
    */

    val aNumber = 43
    val numberDescription: String = aNumber match {
        case 1 => "gold"
        case 2 => "silver"
        case 3 => "bronze"
        case _ => "nothing"
    }
    // MatchError if no pattern matches

    case class City(name: String, country: String)
    case class Person(name: String, age: Int, residency: City)
    object MySingleton

    val unknownValue: Any = 1
    val myPatternMatch = unknownValue match {
        // constants
        case 1 => "number one"
        case "1" => "first string"
        // objects
        case MySingleton => "a singleton"
        // case classes
        case Person(n, a, City("Bucharest", _)) => s"a person with name $n and age $a"
        // name pattern portions
        case Person(n, a, c @ City(_, "Romania")) => s"this person lives in $c"
        // filters/if-guards
        case Person(_, a, _) if a >= 18 => "an adult"
        // tuples
        case (1, _) => "pair with 1"
        // lists
        case List(1,2,3) => "first 3 numbers"
        // type-checks, performed at runtime
        case _: Person => "a person"
        // match anything with name = "variable"
        case something => "match anything, called 'something'"
        // match anything = wildcard - place it at the end
        case _ => "anything else"
    }

    val weirdPattern = unknownValue match {
        case Person(_, a, _) if a >= 18 => "an adult"
        case Person(_, a, _) if a < 18 => "under-age"
        case _ => "something"
    }

    // refactor to:

    val normalPattern = unknownValue match {
        case Person(_, a, _)  =>
            if (a >= 18) "an adult"
            else if (a < 3) "infant"
            else "under-age"
        case _ => "something"
    }

    // TODO exercise on typed lists
    val aList = List(1,2,3)
    val listDescription = aList match {
        case _: List[String] => "list of strings"
        case _: List[Int] => "list of integers"
        case _ => "something else"
    }
    // generic types are erased

  /**
    * Options
    *   - construction, map, flatMap, filter, orElse, getOrElse
    */

    def getUnsafeString(): String = null
    // getUnsafeString().length // NPE

    val anOption: Option[String] = Option(getUnsafeString()) // None
    val noneOption: Option[String] = None // a real value
    val aNonEmptyOption: Option[String] = Some("Scala")
    val aNonEmptyOption_v2: Option[String] = Option("Scala") // <-- recommended

    // map
    val scalaLength = aNonEmptyOption.map(x => x.length) // Option[Int] == Some(5)
    // flatMap
    val scalaOptionLength = aNonEmptyOption.flatMap(x => Option(x.length)) // Option[Int] == Some(5)
    // filter
    val scalaFilter = aNonEmptyOption.filter(x => x.length > 10) // None
    val javaOption = Option("Java")

    // for-comprehensions
    val languages = for {
        s <- aNonEmptyOption
        j <- javaOption
    } yield s"$j vs $s"
    val languages_v2 = aNonEmptyOption.flatMap(s => javaOption.map(j => s"$j vs $s")) // identical

    val favLanguage: String = aNonEmptyOption.getOrElse("Scala")
    // val getter = aNonEmptyOption.get // DO NOT USE THIS

    // Some(getUnsafeString()) // DO NOT USE THIS // Some(null)
    Option(getUnsafeString()) // OK

    val optionPM = aNonEmptyOption match {
        case None => "nothing here"
        case Some(language) => s"favorite language is $language"
    }

    // TODO exercise with unsafe APIs and Option composition
    val config: Map[String, String] = Map(
        "host" -> "192.168.1.1",
        "port" -> "8081"
    )

    class Connection {
        def connect = "Connected" // connect to some server
    }

    object Connection {
        val random = new Random(System.currentTimeMillis())

        def apply(host: String, port: String): Connection =
            if (random.nextBoolean()) new Connection
            else null
    }

    // Task: obtain a connection and call its connect method if it's not null
    def connectMaybe(host: String, port: String): Unit = {
        val connectionOption: Option[Connection] = Option(Connection(host, port))
        connectionOption match {
            case Some(conn) =>
                println(conn.connect)
            case None =>
                println("Connection failed, retrying...")
                connectMaybe(host, port)
        }
    }

    def connectMaybe_v2(): Unit = {
        val connectionResult: Option[String] = for {
            host <- config.get("host") // Option[String]
            port <- config.get("port") // Option[String]
            conn <- Option(Connection(host, port))
        } yield conn.connect

        connectionResult.foreach(println)
    }

  /**
    * Exceptions and Try
    *   - throwing; throwing returns Nothing
    *   - catching exceptions
    *   - wrapping try/catches into Try
    *   - map, flatMap, filter
    */

    val potentialException = try {
        // code might crash
        throw new NullPointerException
    } catch {
        // best practice: order cases most specific -> most general
        case e: NullPointerException => "caught NPE"
        case _: RuntimeException => "caught some other kind of exception"
        case _ => "caught a cat"
    }
    // no checked exceptions

    val aFailure: Try[Int] = Try(throw new RuntimeException) // Failure(new RuntimeException)
    val aSuccess: Try[Int] = Try(45) // Success(45)

    // pattern match
    val processSuccess = aSuccess match {
        case Success(value) => s"succeeded with $value"
        case Failure(ex) => s"failed: $ex"
    }

    // map
    val moreSuccess = aSuccess.map(_ * 10) // Success(450)
    // flatMap
    val processSuccess2 = aSuccess.flatMap(x => Try(x / 0)) // Failure(new ArithmeticException)
    // filter
    val isSuccessEnough = aSuccess.filter(_ > 100) // Failure(new NoSuchElementException)

    val superSuccess = for {
        s1 <- aSuccess
        s2 <- moreSuccess
    } yield s1 + s2

    val superSuccess_v2 = aSuccess.flatMap(s1 => moreSuccess.map(s2 => s1 + s2)) // identical

    // TODO exercise with unsafe APIs and try composition
    val newHost = "192.168.1.1"
    val newPort = "8080"

    class TConnection {
        val random = new Random(System.currentTimeMillis())

        def fetch(url: String): String = {
            if (random.nextBoolean()) "<html>This is good</html>"
            else throw new RuntimeException(s"Connection interrupted to $url")
        }
    }

    object HttpService {
        val random = new Random(System.currentTimeMillis())

        def getConnection(host: String, port: String): TConnection =
            if (random.nextBoolean()) new TConnection
            else throw new RuntimeException("Connection cannot be obtained")
    }

    def defensiveVersion(): Unit = {
        try {
            val conn = HttpService.getConnection(newHost, newPort)
            try {
                println(conn.fetch("google.com"))
            } catch {
                case e => println(s"Error fetching page: $e")
            }
        } catch {
            case e => println(s"Error connection: $e")
        }
    }

    def unsafeTryForComprehension(): Unit = {
        val potentialHtml: Try[String] = for {
            tConnection <- Try(HttpService.getConnection(newHost, newPort))
            webPage <- Try(tConnection.fetch("google.com"))
        } yield webPage

        potentialHtml match {
            case Success(page) => println(page)
            case Failure(ex) => println(s"Error: $ex")
        }
    }

  /**
    * TODO: The ASCII Art mini-project
    */

  /**
    * PartialFunctions
    */

  def getMedal(number: Int): String = number match {
    case 1 => "gold"
    case 2 => "silver"
    case 3 => "bronze"
  }

  val medalFunction: PartialFunction[Int, String] = {
    case 1 => "gold"
    case 2 => "silver"
    case 3 => "bronze"
  }
  // equivalent with (x: Int) => x match { case 1 => ... }

  val medal = medalFunction(2) // "silver"
  val willIGetMedal = medalFunction.isDefinedAt(43)

  // composing PFs
  val honorableMentions: PartialFunction[Int, String] = {
    case 4 => "first mention"
    case 5 => "second mention"
  }
  val composedFunction: PartialFunction[Int, String] = medalFunction.orElse(honorableMentions)

  // curly syntax
  List(1,2,3).map { x =>
    // block of code
    x + 1
  }

  List(1,2,3).map { // equivalent with List(1,2,3).map { x => x match { ... } }
    case 1 => "gold"
    case 2 => "silver"
    case 3 => "bronze"
  } // List("gold", "silver", "bronze")

  /**
    * Partially Applied Functions (PAFs)
    */

  def incrementMethod(x: Int): Int = x + 1 // method
  incrementMethod(3) // 4

  val incrementFunction: Int => Int = x => x + 1
  incrementFunction(3) // 4

  // turn methods into functions = eta-expansion = partially applied function
  val incrementMethodAsFunction = incrementMethod _ // x => incrementMethod(x)
  val incrementFunctionExplicit = (x: Int) => incrementMethod(x) // same

  val incrementMethodAsFunction_v2: Int => Int = incrementMethod // automatic eta-expansion
  List(1,2,3).map(incrementMethod) // automatic eta-expansion

  // curried PAFs
  def curriedSummer(x: Int)(y: Int): Int = x + y
  val add2 = curriedSummer(2) _ // (y: Int) => 2 + y
  List(1,2,3).map(add2) // List(3,4,5)

  // multi-arg PAFs
  def simpleSummer(x: Int, y: Int): Int = x + y
  val sumFunction = simpleSummer _ // (x: Int, y: Int) => simpleSummer(x, y)

  /**
    * Lazy Evaluation
    */
   lazy val x = {
     println("computing lazy value")
     42
   }

  println(x)
  println(x) // memoized

  def printValue(x: => Int): Unit = {
    println(x)
    println(x) // not memoized - computed every time
  }

  printValue {
    println("computing by name value")
    45
  }

  def callByNeed(n: => Int): Int = {
    lazy val t = n // delay evaluation of method argument
    t + t + t // memoize
  }

  def generateMagicValue() = {
    println("waiting...")
    Thread.sleep(1000)
    65
  }

  println(callByNeed(generateMagicValue()))

  def lessThan30(n: Int): Boolean = {
    println(s"is $n less than 30?")
    n < 30
  }

  def greaterThan20(n: Int): Boolean = {
    println(s"is $n greater than 20?")
    n > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30) // List(1, 25, 5, 23)
  val gt20 = lt30.filter(greaterThan20) // List(25, 23)
  println(gt20)

  val lt30Lazy = numbers.withFilter(lessThan30)
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  gt20Lazy.foreach(println) // filters are executed lazily

  for {
    n <- List(1,2,3) if n % 2 == 0 // .withFilter(n % 2) == 0 -> lazily evaluated
  } yield n + 1



  /**
    TODO: implement a lazily evaluated, singly linked list of elements.
    (I'll define the MyStream abstract class)
    TODO (time permitting): Compute the infinite list of primes with Eratosthenes' sieve
    */

  /*
    [13,17,19,23,.......
    [2,3,5,7,11...
   */
  def eratosthenesSieve(naturals: ZList[Int]): ZList[Int] = ???

  /**
    * Futures
    *   - construction, map, flatMap, filter, onComplete
    *   - promises
    */

  val threadPool = Executors.newFixedThreadPool(8)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(threadPool)


  val aFuture = Future { // single argument, passed by name
    // async computation
    42
  }

  // monitor a future for completion
  aFuture.onComplete {
    case Success(someValue) => println(s"Future completed: $someValue")
    case Failure(ex) => println(s"Future failed: $ex")
  }

  // map
  val aMappedFuture = aFuture.map(_ * 10) // _ * 10 will run on SOME thread
  // flatMap
  val aFlatMappedFuture = aFuture.flatMap(value => Future(value * 10)) // Future[Int] - will run on SOME thread
  // filter
  val aFilteredFuture = aFuture.filter(_ % 10 == 2) // Future[Int] - will run on SOME thread, may fail with NoSuchElementException if predicate failds
  // for-comprehensions
  val favoriteLanguageFuture = Future("Scala")
  val meaningOfLife = for {
    number <- aFuture
    lang <- favoriteLanguageFuture
  } yield s"$lang:$number" // Future[String]
  val meaningOfLife_v2 = aFuture.flatMap(number => favoriteLanguageFuture.map(lang => s"$lang:$number")) // identical

  // wait for future to finish
  import scala.concurrent.duration._
  def synchronousFutureCode(): Unit = {
    // do NOT use blocking calls unless necessary
    val waited = Await.result(aFuture, 3.seconds) // blocks calling thread
    // do NOT inspect futures
    val currentValue = aFuture.value // value at the current moment
  }

  // recovering
  val recoveredFuture: Future[Int] = aFuture.recover[Int] { // callback runs on SOME thread
    case e: RuntimeException => 67
  }
  // recovering asynchronously
  val recoveredFutureAsync: Future[Int] = aFuture.recoverWith[Int] {
    case e: RuntimeException => Future(67)
  }

  // promises
  val promise: Promise[Int] = Promise[Int]()
  promise.complete(Success(45)) // completes the wrapped Future synchronously, now

  def demoProdCons(): Unit = {
    val prodConsPromise = Promise[Int]()
    // thread 1 - "consumer"
    prodConsPromise.future.onComplete {
      case Success(value) => println(s"[consumer] I've got $value")
    }

    // thread 2 - "producer"
    val producer = new Thread(() => {
      println("[producer] crunching numbers...")
      Thread.sleep(1000)
      // fulfill promise
      prodConsPromise.success(45)
      println("[producer] done!")
    })

    producer.start()
    Thread.sleep(2000)
  }

  def fulfillImmediately[A](value: A): Future[A] =
    Future.successful(value)
  def inSequence[A](first: => Future[A], second: => Future[A]): Future[A] =
    first.flatMap(_ => second)
  def first[A](f: Future[A], g: Future[A]): Future[A] = {

    def busyWait(f: Future[A], g: Future[A]): Future[A] =
      if (f.isCompleted) f
      else if (g.isCompleted) g
      else busyWait(f, g)

    val promise = Promise[A]()
    f.onComplete(result => promise.tryComplete(result))
    g.onComplete(result => promise.tryComplete(result))
    promise.future
  }

  def last[A](f: Future[A], g: Future[A]): Future[A] = {
    val firstPromise = Promise[A]()
    val secondPromise = Promise[A]()
    f.onComplete(result => if (!firstPromise.tryComplete(result)) secondPromise.complete(result))
    g.onComplete(result => if (!firstPromise.tryComplete(result)) secondPromise.complete(result))
    secondPromise.future
  }

  def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] =
    action().filter(condition).recoverWith {
      case _ => retryUntil(action, condition)
    }

  /**
  TODO Future exercises:
      1) fulfill a future IMMEDIATELY with a value
      2) inSequence(fa, fb)
      3) first(fa, fb) => new future with the first value of the two futures
      4) last(fa, fb) => new future with the last value
      5) retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
    */

  def main(args: Array[String]): Unit = {
//      val naturals = ZList.from(0)(_ + 1)
//      println(naturals.takeAsList(1000))
//
//      Thread.sleep(1000)
//      demoProdCons()

//    inSequence(
//      Future {
//        println("first")
//        43
//      },
//      Future {
//        println("second")
//        32
//      }
//    ).foreach(println)

    // first/last test
    val slowPromise = Future {
      Thread.sleep(500)
      1
    }

    val fastPromise = Future {
      Thread.sleep(200)
      2
    }

    last(slowPromise, fastPromise).foreach(println)
    val random = new Random(System.currentTimeMillis())

    // retryUntil test
    val action = () => Future {
      println("Generating integer")
      random.nextInt()
    }

    val condition = (x: Int) => {
      val endsInZero = x % 10 == 0
      if (endsInZero) println(s"$x passing predicate")
      else println(s"$x not passing predicate")
      endsInZero
    }

    retryUntil(action, condition).foreach(println)
  }
}

abstract class ZList[+A] {
  def head: A
  def tail: ZList[A]
  def isEmpty: Boolean

  def ::[B >: A](e: B): ZList[B] = new ZCons(e, this)
  def ++[B >: A](anotherList: ZList[B]): ZList[B]

  def map[B](f: A => B): ZList[B]
  def flatMap[B](f: A => ZList[B]): ZList[B]
  def filter(p: A => Boolean): ZList[A]

  def take(n: Int): ZList[A]
  def takeAsList(n: Int): List[A]
}

object ZEmpty extends ZList[Nothing] {
  override def head = throw new NoSuchElementException
  override def tail = throw new NoSuchElementException
  override def isEmpty = true
  override def ++[B >: Nothing](anotherList: ZList[B]) = anotherList
  override def map[B](f: Nothing => B) = ZEmpty
  override def flatMap[B](f: Nothing => ZList[B]) = ZEmpty
  override def filter(p: Nothing => Boolean): ZList[Nothing] = ZEmpty
  override def take(n: Int) = ZEmpty
  override def takeAsList(n: Int) = List()
}

class ZCons[+A](hd: => A, tl: => ZList[A]) extends ZList[A] {
  // hint: use call-by-need
  override lazy val head = hd
  override lazy val tail = tl

  override def isEmpty = false
  override def ++[B >: A](anotherList: ZList[B]) =
    new ZCons[B](head, tail ++ anotherList) // tail ++ anotherList is delayed
  override def map[B](f: A => B) =
    new ZCons[B](f(head), tail.map(f)) // lazily evaluated
  override def flatMap[B](f: A => ZList[B]) =
    f(head) ++ tail.flatMap(f) // lazy eval
  override def filter(p: A => Boolean): ZList[A] =
    if (p(head)) new ZCons(head, tail.filter(p))
    else tail.filter(p) // does NOT preserve lazy eval!!

  override def take(n: Int) =
    if (n <= 0) ZEmpty
    else new ZCons(head, tail.take(n-1))

  override def takeAsList(n: Int): List[A] =
    if (n <= 0) List()
    else head :: tail.takeAsList(n - 1)
}

object ZList {
  // ZList.from[Int](0)(x => x + 1) == [1,2,3,4,....infinity]
  def from[A](start: A)(generator: A => A): ZList[A] =
    new ZCons[A](start, from(generator(start))(generator))
}


