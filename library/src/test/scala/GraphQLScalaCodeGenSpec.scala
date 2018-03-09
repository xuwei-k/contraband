package sbt.contraband

import org.scalatest._
import java.io.File
import parser.SchemaParser
import GraphQLExample._
import scala.util.Success

class GraphQLScalaCodeGenSpec extends FlatSpec with Matchers with Inside with EqualLines {
  "generate(Enumeration)" should "generate a simple enumeration" in {
    val Success(ast) = SchemaParser.parse(simpleEnumerationExample)
    // println(ast)
    val code = mkScalaCodeGen.generate(ast)
    code.head._2.unindent should equalLines(
      """package com.example
        |/** Example of an enumeration */
        |sealed abstract class EnumExample extends Serializable
        |object EnumExample {
        |  // Some extra code
        |  /** First symbol */
        |  case object First extends EnumExample
        |
        |  case object Second extends EnumExample
        |}""".stripMargin.unindent)
  }

  "generate(Record)" should "generate a record" in {
    val Success(ast) = SchemaParser.parse(recordExample)
    // println(ast)
    val code = mkScalaCodeGen.generate(ast)
    code.head._2.unindent should equalLines(
      """package com.example
        |/** Example of a type */
        |final class TypeExample private (
        |val field: Option[java.net.URL]) extends Serializable {
        |  // Some extra code
        |  override def equals(o: Any): Boolean = o match {
        |    case x: TypeExample => (this.field == x.field)
        |    case _ => false
        |  }
        |  override def hashCode: Int = {
        |    37 * (37 * (17 + "com.example.TypeExample".##) + field.##)
        |  }
        |  override def toString: String = {
        |    "TypeExample(" + field + ")"
        |  }
        |  private[this] def copy(field: Option[java.net.URL] = field): TypeExample = {
        |    new TypeExample(field)
        |  }
        |  def withField(field: Option[java.net.URL]): TypeExample = {
        |    copy(field = field)
        |  }
        |  def withField(field: java.net.URL): TypeExample = {
        |    copy(field = Option(field))
        |  }
        |}
        |object TypeExample {
        |  def apply(field: Option[java.net.URL]): TypeExample = new TypeExample(field)
        |  def apply(field: java.net.URL): TypeExample = new TypeExample(Option(field))
        |}""".stripMargin.unindent)
  }

  it should "generate Map[String, String] from StringStringMap" in {
    val Success(ast) = SchemaParser.parse(stringStringMapExample)
    // println(ast)
    val code = mkScalaCodeGen.generate(ast)
    code.head._2.unindent should equalLines(
      """package com.example
        |/** Example of a type */
        |final class TypeExample private (
        |val field: scala.collection.immutable.Map[String, String]) extends Serializable {
        |  override def equals(o: Any): Boolean = o match {
        |    case x: TypeExample => (this.field == x.field)
        |    case _ => false
        |  }
        |  override def hashCode: Int = {
        |    37 * (37 * (17 + "com.example.TypeExample".##) + field.##)
        |  }
        |  override def toString: String = {
        |    "TypeExample(" + field + ")"
        |  }
        |  private[this] def copy(field: scala.collection.immutable.Map[String, String] = field): TypeExample = {
        |    new TypeExample(field)
        |  }
        |  def withField(field: scala.collection.immutable.Map[String, String]): TypeExample = {
        |    copy(field = field)
        |  }
        |}
        |object TypeExample {
        |  def apply(field: scala.collection.immutable.Map[String, String]): TypeExample = new TypeExample(field)
        |}""".stripMargin.unindent)
  }

  it should "grow a record from 0 to 1 field" in {
    val Success(ast) = SchemaParser.parse(growableAddOneFieldExample)
    // println(ast)
    val code = mkScalaCodeGen.generate(ast)

    code.head._2.unindent should equalLines (
      """package com.example
        |final class Growable private (
        |  val field: Option[Int]) extends Serializable {
        |  private def this() = this(Option(0))
        |  override def equals(o: Any): Boolean = o match {
        |    case x: Growable => (this.field == x.field)
        |    case _ => false
        |  }
        |  override def hashCode: Int = {
        |    37 * (37 * (17 + "com.example.Growable".##) + field.##)
        |  }
        |  override def toString: String = {
        |    "Growable(" + field + ")"
        |  }
        |  private[this] def copy(field: Option[Int] = field): Growable = {
        |    new Growable(field)
        |  }
        |  def withField(field: Option[Int]): Growable = {
        |    copy(field = field)
        |  }
        |  def withField(field: Int): Growable = {
        |    copy(field = Option(field))
        |  }
        |}
        |object Growable {
        |  def apply(): Growable = new Growable()
        |  def apply(field: Option[Int]): Growable = new Growable(field)
        |  def apply(field: Int): Growable = new Growable(Option(field))
        |}
        |""".stripMargin.unindent)
  }

  it should "grow a record from 0 to 2 field" in {
    val Success(ast) = SchemaParser.parse(growableZeroToOneToTwoFieldsExample)
    // println(ast)
    val code = mkScalaCodeGen.generate(ast)

    code.head._2.unindent should equalLines (
      """package com.example
        |final class Foo private (
        |  val x: Option[Int],
        |  val y: Vector[Int]) extends Serializable {
        |  private def this() = this(None, Vector())
        |  private def this(x: Option[Int]) = this(x, Vector())
        |  override def equals(o: Any): Boolean = o match {
        |    case x: Foo => (this.x == x.x) && (this.y == x.y)
        |    case _ => false
        |  }
        |  override def hashCode: Int = {
        |    37 * (37 * (37 * (17 + "com.example.Foo".##) + x.##) + y.##)
        |  }
        |  override def toString: String = {
        |    "Foo(" + x + ", " + y + ")"
        |  }
        |  private[this] def copy(x: Option[Int] = x, y: Vector[Int] = y): Foo = {
        |    new Foo(x, y)
        |  }
        |  def withX(x: Option[Int]): Foo = {
        |    copy(x = x)
        |  }
        |  def withX(x: Int): Foo = {
        |    copy(x = Option(x))
        |  }
        |  def withY(y: Vector[Int]): Foo = {
        |    copy(y = y)
        |  }
        |}
        |object Foo {
        |  def apply(): Foo = new Foo()
        |  def apply(x: Option[Int]): Foo = new Foo(x)
        |  def apply(x: Int): Foo = new Foo(Option(x))
        |  def apply(x: Option[Int], y: Vector[Int]): Foo = new Foo(x, y)
        |  def apply(x: Int, y: Vector[Int]): Foo = new Foo(Option(x), y)
        |}
        |""".stripMargin.unindent)
  }

  "generate(Interface)" should "generate an interface with one child" in {
    val Success(ast) = SchemaParser.parse(intfExample)
    val code = mkScalaCodeGen.generate(ast)
    code.head._2.unindent should equalLines(
      """package com.example
        |/** Example of an interface */
        |sealed abstract class InterfaceExample(
        |val field: Option[Int]) extends Serializable {
        |  // Some extra code
        |  override def equals(o: Any): Boolean = o match {
        |    case x: InterfaceExample => (this.field == x.field)
        |    case _ => false
        |  }
        |  override def hashCode: Int = {
        |    37 * (37 * (17 + "com.example.InterfaceExample".##) + field.##)
        |  }
        |  override def toString: String = {
        |    "InterfaceExample(" + field + ")"
        |  }
        |}
        |object InterfaceExample {
        |}
        |final class ChildType private (
        |  val name: Option[String],
        |  field: Option[Int]) extends com.example.InterfaceExample(field) with Serializable {
        |  override def equals(o: Any): Boolean = o match {
        |    case x: ChildType => (this.name == x.name) && (this.field == x.field)
        |    case _ => false
        |  }
        |  override def hashCode: Int = {
        |    37 * (37 * (37 * (17 + "com.example.ChildType".##) + name.##) + field.##)
        |  }
        |  override def toString: String = {
        |    "ChildType(" + name + ", " + field + ")"
        |  }
        |  private[this] def copy(name: Option[String] = name, field: Option[Int] = field): ChildType = {
        |    new ChildType(name, field)
        |  }
        |  def withName(name: Option[String]): ChildType = {
        |    copy(name = name)
        |  }
        |  def withName(name: String): ChildType = {
        |    copy(name = Option(name))
        |  }
        |  def withField(field: Option[Int]): ChildType = {
        |    copy(field = field)
        |  }
        |  def withField(field: Int): ChildType = {
        |    copy(field = Option(field))
        |  }
        |}
        |
        |object ChildType {
        |  def apply(name: Option[String], field: Option[Int]): ChildType = new ChildType(name, field)
        |  def apply(name: String, field: Int): ChildType = new ChildType(Option(name), Option(field))
        |}""".stripMargin.unindent)
  }

  it should "generate messages" in {
    val Success(ast) = SchemaParser.parse(messageExample)
    val code = mkScalaCodeGen.generate(ast)
    code.head._2.unindent should equalLines(
      """package com.example
        |sealed abstract class IntfExample(
        |  /** I'm a field. */
        |  val field: Option[Int]) extends Serializable {
        |  /**
        |   * A very simple example of a message.
        |   * Messages can only appear in interface definitions.
        |   * @param arg0 The first argument of the message.
        |                 Make sure it is awesome.
        |   * @param arg1 This argument is not important, so it gets single line doc.
        |   */
        |  def messageExample(arg0: => Vector[Int], arg1: Option[Boolean]): Vector[Int]
        |  override def equals(o: Any): Boolean = o match {
        |    case x: IntfExample => (this.field == x.field)
        |    case _ => false
        |  }
        |  override def hashCode: Int = {
        |    37 * (37 * (17 + "com.example.IntfExample".##) + field.##)
        |  }
        |  override def toString: String = {
        |    "IntfExample(" + field + ")"
        |  }
        |}
        |
        |object IntfExample {
        |}
        |""".stripMargin.unindent
    )
  }

  it should "generate with customization" in {
    val Success(ast) = SchemaParser.parse(customizationExample)
    val code = mkScalaCodeGen.generate(ast)
    code.head._2.unindent should equalLines(
      """package com.example
        |/** Example of an interface */
        |sealed abstract class IntfExample(
        |  val field: Option[Int]) extends Interface1 with Interface2 with Serializable {
        |  // Some extra code...
        |  override def equals(o: Any): Boolean = o match {
        |    case x: IntfExample => (this.field == x.field)
        |    case _ => false
        |  }
        |  override def hashCode: Int = {
        |    37 * (37 * (17 + "com.example.IntfExample".##) + field.##)
        |  }
        |  override def toString: String = {
        |    return "custom";
        |  }
        |}
        |
        |object IntfExample extends CompanionInterface1 with CompanionInterface2 {
        |  // Some extra companion code...
        |}
        |""".stripMargin.unindent
    )
  }

  def mkScalaCodeGen: ScalaCodeGen =
    new ScalaCodeGen(javaLazy, CodeGen.javaOptional, CodeGen.instantiateJavaOptional, scalaArray, genFileName,
        scalaSealProtocols = true, scalaPrivateConstructor = true, wrapOption = true)
  val javaLazy = "com.example.Lazy"
  val outputFile = new File("output.scala")
  val scalaArray = "Vector"
  val genFileName = (_: Any) => outputFile
}
