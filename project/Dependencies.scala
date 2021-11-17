import sbt._
import Keys._

object Dependencies {
  val scala212 = "2.12.15"
  val scala213 = "2.13.7"

  val sjsonNewScalaJson = "com.eed3si9n" %% "sjson-new-scalajson" % "0.9.1" cross CrossVersion.for3Use2_13
  val scalaTest         = "org.scalatest" %% "scalatest" % "3.2.10"
  val parboiled         = "org.parboiled" %% "parboiled" % "2.3.0" cross CrossVersion.for3Use2_13
  val diffutils         = "com.googlecode.java-diff-utils" % "diffutils" % "1.3.0"
  val verify            = "com.eed3si9n.verify" %% "verify" % "1.0.0"
}
