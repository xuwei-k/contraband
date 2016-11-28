import sbt._

object Dependencies {
  val sjsonnewCore = "com.eed3si9n" %% "sjson-new-core" % "0.5.0"

  private val jsonTuples = Seq(
    ("org.spire-math", "jawn-parser", "0.10.4"),
    ("org.spire-math", "jawn-json4s", "0.10.4")
  )

  val jsonDependencies = jsonTuples map {
    case (group, mod, version) => (group %% mod % version).exclude("org.scala-lang", "scalap")
  }

  val scalaCheckVersion = "1.13.4"
  val junitInterface    = "com.novocode" % "junit-interface" % "0.11"
  val scalaCheck        = "org.scalacheck" %% "scalacheck" % scalaCheckVersion
  val scalaTest         = "org.scalatest" %% "scalatest" % "3.0.1"
  val parboiled         = "org.parboiled" %% "parboiled" % "2.1.3"
  val diffutils         = "com.googlecode.java-diff-utils" % "diffutils" % "1.3.0"
}
