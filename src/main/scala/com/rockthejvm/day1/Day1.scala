package com.rockthejvm.day1

import scala.annotation.tailrec

object Day1 {

  /**
    * vals, vars, types, type inference
    */

  val helloOrange: String = "Hello, Orange!" // final String helloOrange = "hello, Orange";
  // best practice: pentru API-uri mentionati tipul de date

  /**
      Expressions
        - operators
        - if/else
        - code blocks
        - the Unit type
    */

  val simpleMathExpression: Int = 2 + 3 // Int = java int
  val anIfExpression: String = if (2 > 3) "bigger" else "smaller" // 2 > 3 ? "bigger": "smaller"
  val aCodeBlock = {
    // scope
    val innerVal = 2
    // definitii, clase, functii, valori
    innerVal + 10 // ultima expresie = valoarea blocului
  }

  // Int, Double, Float, Boolean, Char, Long, Short
  // String

  val aComposedString = "hello" + "Orange"

  // Unit == "void"
  val aUnitVal: Unit = println("some string")
  val theUnit: Unit = ()

  /**
    * Functions
    *   - functions inside functions
    *   - (time permitting) ex: isPrime
    */

  def myFunction(intArg: Int, stringArg: String): String = // O SINGURA EXPRESIE
    stringArg + intArg

  def myComplexFunction(intArg: Int): Int = {
    intArg + 2
  }

  // repetitiv = recursivitate
  def isPrime(n: Int): Boolean = {
    def isPrimeHelper(n: Int, potentialDivisor: Int): Boolean =
      if (potentialDivisor >= n/2) true
      else (n % potentialDivisor != 0) && isPrimeHelper(n, potentialDivisor + 1)

    isPrimeHelper(n, 2)
  }

  /**
    * stack/tail recursion
    */
  def factorial(n: Int): Int =
    if (n == 0) 1
    else n * factorial(n - 1)
  // factorial(10000) will crash with SO

  /*
    anotherFactorial(5) =
    factHelper(5, 1) =
    factHelper(4, 5) = // nu se mai aloca alt stack frame
    factHelper(3, 4 * 5) =
    factHelper(2, 3 * 4 * 5) =
    factHelper(1, 2 * 3 * 4 * 5) =
    factHelper(0, 1 * 2 * 3 * 4 * 5) =
    1 * 2 * 3 * 4 * 5 = 120
   */
  def anotherFactorial(n: Int): Int = {
    // apelurile recursive sunt ULTIMELE pe code path-ul lor
    def factHelper(n: Int, accumulator: Int): Int =
      if (n == 0) accumulator
      else factHelper(n - 1, n * accumulator)

    factHelper(n, 1)
  }
  // tail recursion avoids SOs

  /*
    TODO Exercise:
    1) concatenate a string n times
      - with stack recursion
      - with tail recursion
    2) a tail-recursive isPrime function
   */
  def concatenateStrings(string: String, n: Int): String =
    if (n <= 0) ""
    else string + concatenateStrings(string, n - 1)

  /*
    concatenate("string", 3) =
    concatenateAux(3, "") =
    concatenateAux(2, "string") =
    concatenateAux(1, "stringstring") =
    concatenateAux(0, "stringstringstring") =
    "stringstringstring"
   */
  def concatenateStringsTailrec(string: String, n: Int): String = {
    def concatenateAux(n: Int, acc: String): String =
      if (n <= 0) acc
      else concatenateAux(n - 1, string + acc)

    concatenateAux(n, "")
  }

  def isPrimeTailrec(n: Int): Boolean = {
    @tailrec
    def isPrimeHelper(n: Int, potentialDivisor: Int): Boolean =
      if (potentialDivisor >= n/2) true
      else if (n % potentialDivisor != 0) false
      else isPrimeHelper(n, potentialDivisor + 1) // tail position

    isPrimeHelper(n, 2)
  }

  /**
    * Other basics
    *   - s-interpolators
    *   - default arguments
    */

  val meaningOfLife = 42
  val interpolatedString = s"The meaning of life is $meaningOfLife, added 1: ${meaningOfLife + 1}"

  def aFunctionWithDefaultArgs(x: Int, y: Int = 40): Int = x + y
  aFunctionWithDefaultArgs(56) // aFunctionWithDefaultArgs(56, 40)
  aFunctionWithDefaultArgs(56, 98)
  aFunctionWithDefaultArgs(67, y = 68) // naming arguments

  /**
    * OO basics
    *   - parameters vs fields
    *
    * Method notation
    *   - infix methods
    *   - operators
    *   - apply()
    */

  class Person(val name: String, val age: Int) { // constructor principal
    def this(name: String) = // constructor auxiliar
      this(name, 0)

    // can refer to name, age
    // constructor args != fields

    // methods
    def greet(): String = s"Hi, my name is $name"

    // likes takes 1 arg => infix notation
    def likes(movie: String): String = s"$name likes $movie"

    // acceptable method names
    def ?!!(person: Person): String = s"Hey, ${person.name}, what are you doing?"

    def +(weight: Int): String = "I've gained some weight"

    // left-associative
    def -->:(weight: Int): String = "I've gained some weight"

    def apply(weight: Int): String = "I've gained some weight"
    def apply(x: Int, y: Int) = x + y
  }

  val daniel = new Person("Daniel", 98)
  val mike = new Person("Mike", 78)
  val danielsName = daniel.name
  val danielsGreeting = daniel.greet()

  val danielForrestGump = daniel.likes("Forrest Gump")
  val danielForrestGump2 = daniel likes "Forrest Gump" // same = infix = only for 1-arg methods

  val danielTellsMike = daniel ?!! mike
  val daniel3kg = daniel + 3
  val daniel3kg_v2 = 3 -->: daniel // daniel.-->:(3)
  val aList = 1 :: 2 :: List(3,4,5) // List(3,4,5).::(2).::(1)

  val aSum = 2 + 3
  val aSum2 = 2.+(3) // same

  val daniel3kg_v3 = daniel.apply(3)
  val daniel3kg_v4 = daniel(3) // daniel.apply(3)
  val aSum3 = daniel(4, 5) // daniel is INVOKED like a function

  /**
    * Objects
    *   - singletons
    *   - companions
    *   - the main method
    *   - the "static" concept
    */
  object MySingleton { // singleton pattern
    val aField = 43
    def aMethod() = println("something")
  }

  val singletonField = MySingleton.aField

  // class + object with same name in same file = COMPANIONS
  object Person { // companion object
    // access to private fields of class + viceversa
    val N_HANDS = 2
    // apply factory method
    def apply(name: String): Person = new Person(name, 0)
  }

  val jane = Person("Jane") // Person.apply("Jane")

  // put Java static methods/fields in Scala companion objects
  val nHandsPeople = Person.N_HANDS // "static"

  /**
    * Inheritance
    *   - Animals and Carnivores
    *   - overriding methods and polymorphism
    */

  class Animal {
    def eat() = println("eating")
  }

  class Dog extends Animal { // "extends" works for classes/traits
    override def eat(): Unit = println("dog eating")
  }

  val lassie: Animal = new Dog
  lassie.eat() // dog eating

  trait Carnivore { // trait === "interface" in java
    def eat(animal: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = println("Croc eating animal")
  }

  class Kid(name: String) extends Person(name, 0)
  /*
    Java equivalent:

    class Kid extends Person {
      public Kid(String name) {
        super(name, 0);
      }
    }
   */

  /*
    * TODO Exercise - MyList part 1: create a singly linked list of integers
    *	- needs to have head/tail/isEmpty/add/toString/++
    *	- implemented as Empty/Cons
    */

  /**
    * Case classes: lightweight data structures with (usually) little functionality used for data storage and domain modeling.
    */

  case class City(name: String, country: String, population: Int)
  // instatiation without new
  val bucharest = City("Bucharest", "Romania", 2000000) // City.apply(...)
  // params are fields
  val romania = bucharest.country
  // hashcode & equals
  val bucharest2 = City("Bucharest", "Romania", 2000000)
  val sameBucharest = bucharest == bucharest2 // true; == means EQUALS
  val sameBucharestReference = bucharest eq bucharest2 // false; reference/pointer equality
  // pattern matching
  val historicBucharest = bucharest.copy(population = 1400000) // another City instance

  case object Earth {
    // hashcode, equals, pattern matching, serialization
  }

  // TODO MyList part 2: use case classes for the MyList implementations

  /**
    * Generics
    *   - bounded types with cars
    *   - variance with cars
    */

  class MyCollection[T] // java: class MyCollection<T>
  trait MyMap[K, V] // java: class MyMap<K, V>
  def singleList[A](value: A): List[A] = List(value) // public <A> singleList(value: A): List<A> = ...

  // variance
  class Car
  class Supercar extends Car

  // rule of thumb: Contains A == covariance
  class Garage[+A] // covariance: if A extends B, Garage[A] "extends" Garage[B]
  val car: Car = new Supercar
  val garage: Garage[Car] = new Garage[Supercar] // Garage<? extends Car> = ... new Garage<Supercar>

  // rule of thumb: ACTS ON A == contravariance
  class Mechanic[-A] // contravariance: if A extends B, Mechanic[B] "extends" Mechanic[A]
  val myMechanic: Mechanic[Supercar] = new Mechanic[Car] // Mechanic<? super Car> = ... new Mechanic[Car]

  /* TODO Exercise MyList part 3:
   *   - make the structures generic and covariant
   *   - add map, flatMap and filter with Predicates and Transformers
   */

  val incrementer = new Transformer[Int, Int] {
    override def transform(value: Int) = value + 1
  }

  /*
    java equivalent:

    Transformer incrementer new Transformer<Int, Int>() {
      public int transform(int value) {
        return value + 1
      }
    }
   */

  /**
    * What's a function, really
    *   - the OO-FP dream
    *   - function instance creation
    *   - functions are actually instances of the FunctionX trait with an apply method
    */

  // Int => Int is the same as Function1[Int, Int]
  val incrementerFunction: Int => Int = new Function1[Int, Int] {
    def apply(value: Int): Int = value + 1
  }

  val summingFunction: (Int, Int) => Int = new Function2[Int, Int, Int] {
    override def apply(v1: Int, v2: Int) = v1 + v2
  }

  val one = 1
  val two = incrementerFunction(one)

  // TODO MyList part 4: replace Predicates and Transformers with functions

  /**
    * Anonymous functions
    *   - lambdas demo
    *   - underscore notation
    */
  // lambda = anonymous function instance
  val shortIncrementer: Int => Int = (x: Int) => x + 1 // new Function1[Int, Int] { override ... }
  val shortSummer: (Int, Int) => Int = (x, y) => x + y // new Function2[Int, Int, Int] { override ... }
  //                                    ^^ compiler infers types

  // alternative syntax
  val sketchySyntax: Int => Int = { x =>
    // block of code
    x + 1
  }

  val shortestSummer: (Int, Int) => Int = _ + _ // new Function1[Int, Int] { ... }
  //                                      ^   ^
  //                                      x   y

  // TODO MyList part 5: test the list with anonymous functions

  /**
    * HOFs and curries
    *
    * HOF = higher-order function = function that takes other functions as arguments/returns other functions as results
    * `map`, `flatMap`, `filter` are all HOFs.
    */

  /*
    nTimes(f, 5) =
    x => nTimes(f, 4)(f(x)) = f(f(f(f(f(x)))))

    nTimes(f, 4) =
    x => nTimes(f, 3)(f(x)) = f(f(f(f(x))))

    nTimes(f, 3) =
    x => nTimes(f, 2)(f(x)) = f(f(f(x)))

    nTimes(f, 2) =
    x => nTimes(f, 1)(f(x)) = f(f(x))

    nTimes(f, 1) =
    x => nTimes(f, 0)(f(x)) = f(x)

    nTimes(f, 0) = x => x
   */
  def nTimes(f: Int => Int, n: Int): Int => Int =
    if (n == 0) x => x
    else x => nTimes(f, n - 1)(f(x))


  // TODO MyList part 6: add zipWith and foreach HOFs

  /**
    * map, flatMap, filter, for comprehension
    *   - equivalence between a map chain and a for
    *   - whiteboard test: chessboard
    */

  val numbers = List(1, 2, 3) // List[Int]
  val chars = List('a', 'b', 'c') // List[Char]
  val strings = List("hello", "goodbye") // List[String
  val numbersPlusOne = numbers.map(_ + 1) // [2,3,4]

  /*
    in java:

    for (n : number)
      for (c : chars)
        for (s : Strings)
          result.add(s + n + c)

    return result;

    n for loops = (n - 1) flatMaps + map
   */

  val combination: List[String] = numbers.withFilter(n => n % 2 == 0).flatMap(n => chars.flatMap(c => strings.map(s => s"$s$n$c")))
  val forCombination: List[String] = for {
    n <- numbers if n % 2 == 0
    c <- chars
    s <- strings
  } yield s"$s$n$c" // identical to flatMaps + map chain
  // for-comprehensions are NOT LOOPS
  // for-comprehensions are expressions

  // TODO MyList part 7: add withFilter and do a for-comprehension on our own list
  // takeaway: write map, flatMap, withFilter and you'll support for-comprehensions.

  /**
    * Collections
    *   - lists, arrays, sequences, vectors, sets
    *   - tuples and maps
    */

  // lists
  val aSimpleList = List(1,2,3,4,5)
  val firstElement = aSimpleList.head
  val rest = aSimpleList.tail
  val aPrependedList = 0 :: aSimpleList // aSimpleList.::(0)
  val anExtendedList = 0 +: aSimpleList :+ 6 // [0,1,2,3,4,5,6]

  // arrays
  val aSimpleArray = new Array[Int](10) // new int[10] in java
  aSimpleArray.update(3, 45) // array[3] = 45 in java
  aSimpleArray(3) = 45 // same
  val aMatrix = Array.ofDim[Int](2, 3) // new int[2][3]
  val accessingElement = aMatrix(1)(0)

  // Seq
  val aSequence: Seq[Int] = Seq(1,2,3) // Seq.apply(1,2,3)
  val indexingSequence = aSequence(2) // aSequqnce.apply(2)

  // Vector
  val aVector: Vector[Int] = Vector(1,2,3)
  // same API as Seq, List

  // Sets = no duplicates, based on hashCode + equals
  val aSet: Set[Int] = Set(1,2,3,4,5,6,7,1,2,3) // [1,2,3,4,5,6,7]
  val aSetHas10 = aSet.contains(10) // false
  val aSetHas10_v2 = aSet(10) // false
  val aSetPlus10 = aSet + 10 // [1,2,3,4,5,6,7,10]
  val aSetMinus5 = aSet - 5 // [1,2,3,4,6,7]

  // Ranges
  val aRange = 1 to 1000
  val repeatSomething: Unit = (1 to 1000).foreach(_ => println("hello"))

  // conversions
  val first1000 = aRange.toList // [1,2,3,4,5....1000]
  val removeDupes = List(1,2,3,1,2,3,1,2,3,4,5,6).toSet // [1,2,3,4,5,6]
  val setAsList = aSet.toList

  // tuples
  val aTuple = ("Bon Jovi", "rock", 1982) // (String, String, Int) == Tuple3[String, String, Int]
  val bandName = aTuple._1

  // map
  val phoneBook: Map[String, Int] = Map(
    ("Daniel", 123456),
    "Jane" -> 574252 // ("Jane", 574252)
  )
  val phoneBookHasDaniel = phoneBook.contains("Daniel") // true
  val danielsPhone = phoneBook("Daniel") // 123456, can crash if key is not present
  val mikesPhone = phoneBook.getOrElse("Mike", 0)
  val allNames = phoneBook.keySet // Set[String]
  val allNumbers = phoneBook.values // Iterable[String]
  val newPhonebook = phoneBook.map(tuple => s"${tuple._1}: ${tuple._2}") // Iterable[String]
  val phonebookList = List(("Daniel", 123456), "Jane" -> 574252)
  val phoneBookFromList = phonebookList.toMap // Map[String, Int]


  /**
    * Bonus (time permitting) CBN vs CBV
    */

  /**
    * Bonus 2 (time permitting) pounding tailrec:
    *   - map, flatMap, filter
    *   - reduce
    *   - a sort: merge, quick or insert
    */

  // public static void main(String[] args)
  def main(args: Array[String]): Unit = {
    val first3 = new Cons(1, new Cons(2, new Cons(3, Empty)))
    val fourtosix = new Cons(4, new Cons(5, new Cons(6, Empty)))

    val twoToFour = first3.map { x =>
      // block of code
      x + 1
    } // [2,3,4]

    val twoToFour2 = first3.map(_ + 1) // x => x + 1

    val tenx = (x: Int) => x * 10
    val millionx = nTimes(tenx, 6) // x => tenx(tenx(tenx(tenx(tenx(tenx(x))))))
    println(millionx(2))

    println(first3.zipWith(fourtosix, (x: Int, y: Int) => x + y)) // [5,7,9]
    first3.foreach(println)

    println(combination)

    val allProducts: LList[Int] = for {
      a <- first3
      b <- fourtosix
    } yield a * b
    println(allProducts)

    println(first3.reduce(_ + _)(0))

    val unsorted = 1 :: 4 :: 3 :: 5 :: 2 :: Empty
    println(unsorted.sort(_ - _))
  }


}

// not used
trait Predicate[-A] { // A => Boolean
  def test(value: A): Boolean
}

trait Transformer[-A, +B] { // A => B
  def transform(value: A): B
}

abstract class LList[+A] {
  def ::[B >: A](elem: B): LList[B] = Cons(elem, this)

  // head/tail/isEmpty/add/toString/++
  def head: A // first element
  def tail: LList[A] // list without first element
  def isEmpty: Boolean
  def add[B >: A](elem: B): LList[B] = Cons(elem, this) // public <B super A> add(elem: B)
  def toString: String // string representation, same as in Java
  def ++[B >: A](anotherList: LList[B]): LList[B] // concatenate lists

  // [1,2,3].map(x -> x + 1) = [2,3,4]
  // [1,2,3].map(x -> x.toString) = ["1", "2", "3"]
  def map[B](transformer: A => B): LList[B]

  // [1,2,3].map(x -> [x, x+1]) = [[1,2], [2,3], [3,4]]
  // [1,2,3].flatMap(x -> [x, x+1]) = [1,2,2,3,3,4]
  def flatMap[B](transformer: A => LList[B]): LList[B]

  // [1,2,3].filter(x -> x % 2 == 0) = [2]
  def filter(predicate: A => Boolean): LList[A]
  def withFilter(predicate: A => Boolean): LList[A]

  // [1,2,3].zipWith(["a","b","c"], _ + _) = ["1a", "2b", "3c"]
  // [1,2,3].zipWith([4,5,6], _ + _) = [5, 7, 9]
  def zipWith[B, C](list: LList[B], f: (A, B) => C): LList[C]

  // [1,2,3].foreach(x => println(x)) => prints each element, one per line
  def foreach(f: A => Unit): Unit

  // [1,2,3,4].reverse = [4,3,2,1]
  def reverse: LList[A]

  // [1,2,3,4].reduce(_ + _)(0) = 10 = 1 + 2 + 3 + 4
  def reduce[B >: A](f: (B, B) => B)(zero: B): B

  def sort(comparator: (A, A) => Int): LList[A]
}

// subtype 1: empty list
case object Empty extends LList[Nothing] {
  override def head: Nothing = throw new NoSuchElementException
  override def tail: LList[Nothing] = throw new NoSuchElementException
  override def isEmpty: Boolean = true
  override def toString = "[]"
  override def ++[B >: Nothing](anotherList: LList[B]): LList[B] = anotherList

  override def map[B](transformer: Nothing => B) = Empty
  override def flatMap[B](transformer: Nothing => LList[B]) = Empty
  override def filter(predicate: Nothing => Boolean): LList[Nothing] = Empty
  override def withFilter(predicate: Nothing => Boolean): LList[Nothing] = Empty

  override def zipWith[B, C](list: LList[B], f: (Nothing, B) => C) =
    if (list.isEmpty) Empty
    else throw new RuntimeException("can't zip with empty list")

  override def foreach(f: Nothing => Unit): Unit = ()
  override def reverse = Empty

  override def reduce[B >: Nothing](f: (B, B) => B)(zero: B) = zero
  override def sort(comparator: (Nothing, Nothing) => Int) = Empty
}

// subtype 2: non-empty list
case class Cons[+A](override val head: A, override val tail: LList[A]) extends LList[A] {
  override def isEmpty = false
  override def toString = {
    def aux(list: LList[A], acc: String): String =
      if (list.isEmpty) acc
      else aux(list.tail, s"$acc ${list.head}")

    s"[${aux(this, "")} ]"
  }

  /*
    [1,2,3] ++ [4,5,6] =
    pp([3,2,1], [4,5,6]) =
    pp([2,1], [3,4,5,6]) =
    pp([1], [2,3,4,5,6]) =
    pp([], [1,2,3,4,5,6]) =
    [1,2,3,4,5,6]
   */
  override def ++[B >: A](anotherList: LList[B]): LList[B] = {
    @tailrec
    def plusplus(list: LList[A], acc: LList[B]): LList[B] =
      if (list.isEmpty) acc
      else plusplus(list.tail, Cons(list.head, acc))

    plusplus(this.reverse, anotherList)
    // Cons(head, tail ++ anotherList) // <-- stack-recursive
  }

  /*
    [1,2,3].map(_ + 1) = mt([1,2,3], [])
    = mt([2,3], [2])
    = mt([3], [3,2])
    = mt([], [4,3,2])
    = [4,3,2].reverse
    = [2,3,4]
   */
  override def map[B](transformer: A => B) = {
    def mapTailrec(list: LList[A], acc: LList[B]): LList[B] =
      if (list.isEmpty) acc.reverse
      else mapTailrec(list.tail, Cons(transformer(list.head), acc))

    // Cons(transformer(head), tail.map(transformer)) // <-- stack-recursive
    mapTailrec(this, Empty)
  }

  /*
    [1,2,3].flatMap(x => [x, x + 1]) = fmt([1,2,3], [])
    = fmt([2,3], [1,2])
    = fmt([3], [1,2] ++ [2,3]) = fmt([3], [1,2,2,3])
    = fmt([], [1,2,2,3] ++ [3,4]) =
    = [1,2,2,3,3,4]
   */
  override def flatMap[B](transformer: A => LList[B]) = {
    def flatMapTailrec(list: LList[A], acc: LList[B]): LList[B] =
      if (list.isEmpty) acc
      else flatMapTailrec(list.tail, acc ++ transformer(list.head))

    // transformer(head) ++ tail.flatMap(transformer)
    flatMapTailrec(this, Empty)
  }

  /*
    [1,2,3,4].filter(_ % 2 == 0) =
    ft([1,2,3,4], []) =
    ft([2,3,4], []) =
    ft([3,4], [2]) =
    ft([4], [2]) =
    ft([], [4,2]) =
    [4,2].reverse = [2,4]
   */
  override def filter(predicate: A => Boolean): LList[A] = {
    def filterTailrec(list: LList[A], acc: LList[A]): LList[A] = {
      if (list.isEmpty) acc.reverse
      else if (predicate(head)) filterTailrec(list.tail, Cons(head, acc))
      else filterTailrec(list.tail, acc)
    }

    filterTailrec(this, Empty)

    //    if (predicate(head)) Cons(head, tail.filter(predicate))
    //    else tail.filter(predicate)
  }

  override def withFilter(predicate: A => Boolean): LList[A] = {
    if (predicate(head)) Cons(head, tail.filter(predicate))
    else tail.filter(predicate)
  }

  override def zipWith[B, C](list: LList[B], f: (A, B) => C) = {
    if (list.isEmpty) throw new RuntimeException("can't zip with empty list")
    else Cons(f(head, list.head), tail.zipWith(list.tail, f))
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  /*
    [1,2,3,4].reverse = rt([1,2,3,4], [])
    = rt([2,3,4], [1])
    = rt([3,4], [2,1])
    = rt([4], [3,2,1])
    = rt([], [4,3,2,1])
    = [4,3,2,1]
   */
  override def reverse: LList[A] = {
    @tailrec
    def reverseTailrec(list: LList[A], acc: LList[A]): LList[A] =
      if (list.isEmpty) acc
      else reverseTailrec(list.tail, Cons(list.head, acc))

    reverseTailrec(this, Empty)
  }

  override def reduce[B >: A](f: (B, B) => B)(zero: B) =
    tail.reduce(f)(f(zero, head))

  override def sort(comparator: (A, A) => Int) = {

    /*
      it(3, [], [1,2,4,5]) =
      it(3, [1], [2,4,5]) =
      it(3, [2,1], [4,5]) =
      [2, 1].reverse ++ [3, 4, 5]
      [1,2,3,4,5]
     */
    def insertTailrec(elem: A, smaller: LList[A], greater: LList[A]): LList[A] =
      if (greater.isEmpty || comparator(elem, greater.head) <= 0) smaller.reverse ++ Cons(elem, greater)
      else insertTailrec(elem, greater.head :: smaller, greater.tail)

    def insert(elem: A, list: LList[A]): LList[A] = {
      if (list.isEmpty) Cons(elem, Empty)
      else if (comparator(elem, list.head) > 0) Cons(list.head, insert(elem, list.tail))
      else Cons(elem, list)
    }

    def sortAux(remaining: LList[A], acc: LList[A]): LList[A] =
      if (remaining.isEmpty) acc
      else sortAux(remaining.tail, insertTailrec(remaining.head, Empty, acc))

    sortAux(this, Empty)
  }
}