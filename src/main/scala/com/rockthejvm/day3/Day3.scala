package com.rockthejvm.day3

import com.rockthejvm.day3.JsonSerialization.JSONNumber

import java.sql.Date

object Day3 {

  /**
    * Easy start: dark syntax sugars
    */

  // #1 - single-arg methods + {}
  def singleArgMethod(x: Int): String = "something"
  singleArgMethod(43)
  singleArgMethod {
    // inner scope
    val y = 45
    y + 3
  }
  // examples: Try {...}, Future {...}

  // #2 - single abstract method pattern (since Scala 2.12)
  trait Action {
    def increment(x: Int) = x + 1
    def execute(x: Int, y: Int): Int // single abstract method
  }

  val anAction: Action = (x, y) => x + y // new Action { override def execute(x, y) = x + y }
  val aRunnable: Runnable = () => println("running")

  // #3 - left-associative methods: method name ends in ':'
  // >>>: -->:, #::, +:

  val aList = List(1,2,3)
  val aPrependedList = aList.::(4) // [4,1,2,3]
  val aPrependedListNicer = 4 :: aList
  val superList = 1 :: 2 :: 3 :: aList // ((aList.::(3)).::(2)).::(1)

  // #4 - multi-word method
  class StoryTeller {
    def `told me`(something: String) = println(something)
  }
  val storyTeller = new StoryTeller
  storyTeller.`told me`("Scala is cool!")
  storyTeller `told me` "Scala is cool!"

  // #5 - infix types, for generic types with TWO type arguments
  class Composite[A, B]
  val aComposite: Int Composite String = new Composite[Int, String]

  class ~>[A, B] // type naming
  val anArrow: Int ~> String = new ~>[Int, String]

  // #6 - update
  val anArray = Array(1,2,3)
  anArray.update(1, 35)
  anArray(1) = 35 // same

  class MutableContainer {
    def update(i: Int, arg: String): Unit = println("something")
    def apply(i: Int, arg: String): Unit = println("apply")
    def apply(i: Int): String = "Scala"
  }

  val aMutableContainer = new MutableContainer
  aMutableContainer.update(3, "scala")
  aMutableContainer(3) = "Scala" // same
  aMutableContainer(3, "Scala")
  aMutableContainer(3) // aMutableContainer.apply(3) = "Scala"

  // #7 - mutable containers
  class Mutable {
    private var internalMember = 0
    def member = internalMember // "getter", no args, no ()
    def member_=(newValue: Int): Unit = // "setter"
      internalMember = newValue
  }

  val mutable = new Mutable
  val mutableMember = mutable.member
  mutable.member = 45 // rewritten as mutable.member_=(45)

  /**
    * Advanced Pattern Matching
    */
  class Person(val name: String, val age: Int, confidentialInfo: String)
  object Person {
    def unapply(person: Person): Option[(String, Int)] = {
      if (person.age > 18) None
      else Some((person.name, person.age))
    }

    def unapply(age: Int): Option[String] = {
      if (age < 18) Some("underage")
      else Some("adult")
    }
  }

  object Person2 {
    def unapply(person: Person): Option[(String, Int, String)] = {
      if (person.age > 18) Some((person.name, person.age, "adult"))
      else Some((person.name, person.age, "underage"))
    }
  }

  val daniel = new Person("Daniel", 99, "I love Scala")
  val danielsDescription: String = daniel match {
    case Person(n, a) => s"found a person: $n, $a" // Person.unapply(daniel) => None
    case Person2(n, a, adultStatus) => s"found $adultStatus: $n, $a" // Person2.unapply(daniel)
    case _ => "did not find anyone"
  }

  val age = 45
  val isAdult = age match {
    case Person(adultStatus) => adultStatus // Person.unapply(age): Option(adultStatus)
  }

  // boolean patterns
  object even {
    def unapply(x: Int): Boolean = x % 2 == 0
  }

  object endsIn0 {
    def unapply(x: Int): Boolean = x % 10 == 0
  }

  val aNumber = 100
  val mathProperty = aNumber match {
    case even() => "number is even" // even.unapply(aNumber): Boolean
    case endsIn0() => "number ends in 0"
    case _ => "number is not special"
  }

  // infix patterns
  case class Or[A, B](a: A, b: B)
  val either = Or(2, "Scala") // Or[Int, String]
  val eitherDescription = either match {
    case a Or b => s"$a or $b" // Or(a, b)
  }

  val listDescription = aList match {
    case List(1,2,3) => "first 3 elements"
    case 1 :: somethingElse => "list starts with 1"
  }

  abstract class MyList[+A] {
    def head: A = throw new NoSuchElementException
    def tail: MyList[A] = throw new NoSuchElementException
  }

  object Empty extends MyList[Nothing]
  class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq())
      else unapplySeq(list.tail).map(otherElements => list.head +: otherElements)
  }

  val myList = new Cons(1, new Cons(2, new Cons(3, Empty)))
  myList match {
    case MyList(1,2,3) => "first 3 elements" // MyList.unapplySeq(myList): Option(Seq(1,2,3))
  }

  /**
    * Implicits
    */

  /**
    * 1. implicit values and arguments
    */
  def methodWithImplicitArgument(explicitArgument: Int)(implicit meaningOfLife: Int) =
    explicitArgument + meaningOfLife

  // only ONE implicit per type in this scope
  implicit val answerToEverything: Int = 42
  implicit val stringAnswer: String = "Scala"
  methodWithImplicitArgument(56) // methodWithImplicitArgument(56)(42)
  methodWithImplicitArgument(56)(98)

  // implicit val intOrdering: Ordering[Int] = ....
  List(1,2,3).sorted // applicable for Int, String, Double
  case class Citizen(name: String) {
    def displayCivicBehavior(): String = s"Hi, my name is $name and I'm a good citizen."
  }

  // List(Citizen("Kane"), Citizen("Daniel")).sorted // no implicits found => .sorted not applicable

  /**
    * 2. implicit classes & extension methods/type enrichment
    */
  implicit class MyRichInt(n: Int) { // implicit classes take ONE arg
    def times(string: String): String =
      if (n == 0) ""
      else string + new MyRichInt(n-1).times(string)

//    def *[A](list: List[A]): List[A] =
//      if (n == 0) List()
//      else list ++ (new MyRichInt(n - 1) * list)
  }

  val decoratedInt = new MyRichInt(3)
  val scalax3 = decoratedInt.times("Scala") // "ScalaScalaScala"
  val scalaX3 = 3.times("Scala") // new MyRichInt(3).times("Scala") = "ScalaScalaScala"
  val scalaX3Infix = 3 times "Scala"

  // example: ranges
  val range = 1 to 10
  // example: durations
  import scala.concurrent.duration._
  val duration = 3.seconds

  /**
    * TODO: enrich String so you can do math (+,-,*,/) operations between Strings and Ints like in JavaScript
    */
  // "3" / 2 = 1.5
  implicit class MyRichString(string: String) {
    def +(n: Int): Int = string.toInt + n
    def -(n: Int): Int = string.toInt - n
    def *(n: Int): Int = string.toInt * n
    def /(n: Int): Int = string.toInt / n
  }

  /**
    * 3. implicit methods & conversions
    */
  implicit def fromStringToCitizen(name: String): Citizen = Citizen(name)
  val kane = fromStringToCitizen("Kane")
  kane.displayCivicBehavior() // "Hi, ..."

  "Kane".displayCivicBehavior() // fomStringToCitizen("Kane").displayCivicBehavior()
  // prefer implicit classes for extension methods

  /**
    * TODO:
    *   1. Write an extension method for Int that concatenates a list n times, so that you can write
    *         3 * List(1,2,3) == List(1,2,3,1,2,3,1,2,3)
    *      Write it in two styles: with implicit classes and with implicit defs.
    *   2. Make the following C-style code
    *         if (3) "OK" else "something wrong"
    *      compile in Scala.
    */

  class ListMultiplier[A](n: Int) {
    def *(list: List[A]): List[A] =
      if (n == 0) List()
      else list ++ (new ListMultiplier[A](n - 1) * list)
  }

  implicit def automultiplyList[A](n: Int): ListMultiplier[A] = new ListMultiplier[A](n)

  val repeated3 = 3 * List(1,2,3) // automultiplyList(3) * List(1,2,3) = List(1,2,3,1,2,3,1,2,3)
  val repeated3_v2 = new ListMultiplier[Int](3) * List(1,2,3)
  // implicit classes = classes + implicit defs

  implicit def int2bool(n: Int): Boolean = n > 0
  val cStyleExpr = if (3) "OK" else "something wrong"

  /**
    * 4. Organizing implicits
    */
  val citizens = List(
    Citizen("Jane"),
    Citizen("Kane"),
    Citizen("Daniel")
  )
  /*
    implicit resolution
    - local scope
    - imported scope (including predef imports)
    - companions of ALL types involved in method call
   */

  // 1 - local scope
  //  implicit val alphabeticOrdering: Ordering[Citizen] =
  //    Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)

  // 2 - imported scope
  object MyImplicits {
    implicit val reverseOrdering: Ordering[Citizen] =
      Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) > 0)
  }
  import MyImplicits._

  // 3 - companions of List, Citizen
  object Citizen {
    implicit val alphabeticOrdering: Ordering[Citizen] =
      Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  val sortedCitizens = citizens.sorted // needs implicit Ordering[Citizen]

  /**
    * Implicits application: type classes
    */

  // option #1
  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml = s"<div>$name ($age yo) <a href=" + "\"" + email + "\"/></div>"
  }
  // and other data types

  val user = User("Daniel", 99, "daniel@rockthejvm.com")
  val userDiv = user.toHtml

  /*
    - supported only for some types
    - modify the classes themselves
   */

  // option #2
  object HTMLSerializerPM {
    def convertToHTML(value: Any): String = value match {
      case User(name, age, email) => s"<div>$name ($age yo) <a href=" + "\"" + email + "\"/></div>"
      // add here for other types
      case _ => ""
    }
  }

  /*
    - lose type safety
    - modify the same code every time
    - one implementation
   */

  // option #3 = type classes
  // part 1: define functionality
  trait HTMLSerializer[T] {
    def serializeToHtml(value: T): String
  }

  object HTMLSerializer {
    def serializeToHtml[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serializeToHtml(value)
  }

  // part 2: type class instances
  object UserSerializer extends HTMLSerializer[User] {
    override def serializeToHtml(value: User) =
      s"<div>${value.name} (${value.age} yo) <a href=" + "\"" + value.email + "\"/></div>"
  }
  // same for all other types

  val danielsHtml = UserSerializer.serializeToHtml(user)

  // advantage #1: define implementations for other types
  implicit object DateSerializer extends HTMLSerializer[Date] {
    override def serializeToHtml(value: Date) =
      s"<div>${value.toString}</div>"
  }

  // advantage #2: multiple implementations for the same type
  implicit object SomeOtherUserSerializer extends HTMLSerializer[User] {
    override def serializeToHtml(value: User) =
      s"<div>${value.name}</div>" // some other implementation
  }

  HTMLSerializer.serializeToHtml(user) // implicit HTMLSerializer[User]
  HTMLSerializer.serializeToHtml(Date.valueOf("23-03-2020")) // implicit HTMLSerializer[Date]
  // HTMLSerializer.serializeToHtml(45) // no implicit for Int

  // part 3 - extension methods
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String =
      serializer.serializeToHtml(value)
  }

  println(user.toHTML)
  println(Date.valueOf("23-03-2020").toHTML)

  // context bounds
  def generateHtml[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body> ${serializer.serializeToHtml(content)}  </body></html>"

  def generateHtmlSugar[T : HTMLSerializer](content: T): String =  // implicit serializer: HTMLSerializer[T]
    s"<html><body> ${content.toHTML}  </body></html>"

  /**
    * TODO: implement the Eq type class
    *   - contains a single method testEqual between two values of the same type
    *   - define a few type class instances, e.g. for ints and strings
    *   - implement an extension method === for any type for which there is an implicit Eq[T] in scope
    *   - test the === method on two ints, two strings and two values of different types
    */

  // part 1
  trait Eq[T] {
    def testEqual(a: T, b: T): Boolean
  }

  object Eq {
    def testEqual[T](a: T, b: T)(implicit eq: Eq[T]): Boolean =
      eq.testEqual(a, b)
  }

  // part 2
  implicit object IntEq extends Eq[Int] {
    override def testEqual(a: Int, b: Int) = a == b
  }

  implicit object StringEq extends Eq[String] {
    override def testEqual(a: String, b: String) = a == b
  }

  // part 3
  implicit class EqExtension[T](arg: T) {
    def ===(other: T)(implicit eq: Eq[T]): Boolean =
      eq.testEqual(arg, other)
  }

  1 === 2 // false
  "Scala" === "Scala" // true
  // 1 === "Scala" // does not compile - this is the goal
  // 1 == "Scala" // always false, compiles

  /**
    * Type system mastery: Variance
    *   - "contains T" = covariant, "acts on T" = contravariant
    *   - (massive discussion on variance positions)
    */
  // the variance question:
  // (for List) if A "extends" B, does List[A] "extends" List[B]?
  class Animal
  class Cat extends Animal
  class Dog extends Animal

  // Cat <: Animal => List[Cat] <: List[Animal]
  class SuperList[+T] // covariant
  // Cat <: Animal => Vet[Animal] <: Vet[Cat]
  class Vet[-T] { // contravariant
    def heal(animal: T): Unit = ??? // acts on T
    def getAnimal[S <: T]: S = ???
  }
  val aVet: Vet[Cat] = new Vet[Animal]
  class Invariant[T] // invariant

  // 1 - class VAL fields are in COVARIANT position
  /*
    class TestVet[-T](val animal: T) // covariant position
    val myVet: TestVet[Dog] = new TestVet[Animal](new Cat) // not good.
  */

  // 2.1 - class VAR fields are in COVARIANT position
  /*
    class TestVet[-T](var animal: T)
    val myVet: TestVet[Dog] = new TestVet[Animal](new Cat) // not good.
   */

  // 2.2 - class VAR fields are in CONTRAVARIANT position
  /*
    class Cage[+T](var animal: T) // animal is in CONTRAVARIANT position
    val cage: Cage[Animal] = new Cage[Cat](new Cat)
    cage.animal = new Crocodile // not good.
   */

  // 3 - method argument types
  /*
    class Cage[+T] {
      def keep(animal: T): Unit = ??? // animal is in CONTRAVARIANT position
    }

    val cage: Cage[Animal] = new Cage[Cat]
    cage.keep(new Crocodile) // not good.
   */

  class AList[+T] {
    def add[B >: T](elem: B): AList[B] = ??? // <-- widen argument type
  }

  /*
    val list: AList[Animal] = new AList[Cat]
    list.add[Animal](new Crocodile) // AList[Animal]
    list.add("string") // AList[Object]
   */

  // 4 - method return types
  /*
    class TestVet[-T] {
      def getAnimal: T // <-- return type is in COVARIANT position
    }

    class CatLoverVet extends TestVet[Animal] {
      def getAnimal: Animal = new Cat
    }

    val vet: TestVet[Dog] = new CatLoverVet
    val dog: Dog = vet.getAnimal // not good
   */

  /**
    * Reminder to fill in the 1-minute survey!
    */

  /**
    * Q/A
    */

  def main(args: Array[String]): Unit = {
    println("3" / 2)
    println(repeated3)
    println(repeated3_v2)
    println(sortedCitizens)
  }
}

object JsonSerialization {
  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
    {
      "name": "Daniel",
      "age": 99,
      "email": "daniel@rockthejvm.com"
    }
   */
  val daniel = User("Daniel", 99, "daniel@rockthejvm.com")
  /*
    {
      "content": "I love Scala",
      "date": 537857263563
    }
   */
  val post = Post("I love Scala", new Date(System.currentTimeMillis()))
  val post2 = Post("I've learned a lot", new Date(System.currentTimeMillis()))
  /*
    {
      "user" :
        {
          "name": "Daniel",
          "age": 99,
          "email": "daniel@rockthejvm.com"
        }
      "posts": [
          {
            "content": "I love Scala",
            "date": 537857263563
          },
          {
            "content": "I've learned a lot",
            "date": 527895823895
          },
          ]
    }
   */
  val feed = Feed(daniel, List(post, post2))

  /*
    - JSON numbers
    - JSON strings
    - JSON arrays
    - JSON objects
   */
  abstract class JSONValue {
    def stringify: String
  }

  // TODO: implement intermediary formats for numbers, strings, arrays, objects

  case class JSONNumber(value: Int) extends JSONValue {
    override def stringify = value.toString
  }

  case class JSONString(value: String) extends JSONValue {
    override def stringify = "\"" + value + "\""
  }

  case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify = values.map(_.stringify).mkString("[", ",", "]")
  }

  case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    override def stringify = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }.mkString("{", ",", "}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Daniel"),
    "posts" -> JSONArray(List(
      JSONString("Scala rocks!"),
      JSONNumber(523)
    ))
  ))

  // TODO 2: type class
  // part 1
  trait JSONSerializer[T] {
    def convert(value: T): JSONValue
  }

  object JSONSerializer {
    def convert[T](value: T)(implicit serializer: JSONSerializer[T]): JSONValue =
      serializer.convert(value)
  }

  // TODO: implement TC instances for User, Post, Feed
  implicit object UserConverter extends JSONSerializer[User] {
    override def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }

  implicit object PostConverter extends JSONSerializer[Post] {
    override def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(post.content),
      "createdAt" -> JSONString(post.createdAt.toString)
    ))
  }

  implicit object FeedConverter extends JSONSerializer[Feed] {
    override def convert(feed: Feed): JSONValue = JSONObject(Map(
      "user" -> JSONSerializer.convert(feed.user),
      "posts" -> JSONArray(feed.posts.map(post => JSONSerializer.convert(post)))
    ))
  }

  // part 3 - extension method
  implicit class JSONEnrichment[T](value: T) {
    def toJSON(implicit serializer: JSONSerializer[T]): JSONValue =
      serializer.convert(value)
  }

  def main(args: Array[String]): Unit = {
    println(feed.toJSON.stringify)
  }
}
