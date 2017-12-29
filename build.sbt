import Dependencies._

lazy val pluginSettings = Seq(
  bintrayPackage := "sbt-contraband",
  sbtPlugin := true
)

lazy val root = (project in file(".")).
  enablePlugins(NoPublish, TravisSitePlugin).
  disablePlugins(ScriptedPlugin).
  aggregate(library, plugin).
  settings(
    inThisBuild(List(
      version := "0.3.3-SNAPSHOT",
      organization := "org.scala-sbt",
      crossScalaVersions := Seq("2.12.4", "2.11.12", "2.10.7"),
      scalaVersion := "2.10.7",
      organizationName := "sbt",
      organizationHomepage := Some(url("http://scala-sbt.org/")),
      homepage := Some(url("http://scala-sbt.org/contraband")),
      licenses += ("Apache-2.0", url("https://github.com/sbt/contraband/blob/master/LICENSE")),
      bintrayVcsUrl := Some("git@github.com:sbt/contraband.git"),
      scmInfo := Some(ScmInfo(url("https://github.com/sbt/contraband"), "git@github.com:sbt/contraband.git")),
      developers := List(
        Developer("eed3si9n", "Eugene Yokota", "@eed3si9n", url("https://github.com/eed3si9n")),
        Developer("dwijnand", "Dale Wijnand", "@dwijnand", url("https://github.com/dwijnand")),
        Developer("Duhemm", "Martin Duhem", "@Duhemm", url("https://github.com/Duhemm"))
      ),
      description := "Contraband is a description language for your datatypes and APIs, currently targeting Java and Scala."
    )),
    name := "contraband root",
    siteGithubRepo := "sbt/contraband",
    siteEmail := { "eed3si9n" + "@" + "gmail.com" }
  )

lazy val library = (project in file("library")).
  enablePlugins(KeywordPlugin, SonatypePublish).
  disablePlugins(BintrayPlugin, ScriptedPlugin).
  settings(
    name := "contraband",
    libraryDependencies ++= Seq(parboiled) ++ jsonDependencies.value ++ Seq(scalaTest % Test, diffutils % Test)
  )

lazy val plugin = (project in file("plugin")).
  enablePlugins(BintrayPublish).
  settings(
    // crossBuildingSettings,
    pluginSettings,
    name := "sbt-contraband",
    description := "sbt plugin to generate growable datatypes.",
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    publishLocal := (publishLocal dependsOn (publishLocal in library)).value
  ).
  dependsOn(library)
